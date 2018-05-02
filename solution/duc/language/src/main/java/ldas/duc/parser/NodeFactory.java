/*
 * Copyright (c) 2012, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ldas.duc.parser;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import ldas.duc.DucLanguage;
import ldas.duc.nodes.*;
import ldas.duc.nodes.ExpressionNode;
import ldas.duc.nodes.StatementNode;
import ldas.duc.nodes.access.ReadPropertyNode;
import ldas.duc.nodes.access.ReadPropertyNodeGen;
import ldas.duc.nodes.access.WritePropertyNode;
import ldas.duc.nodes.access.WritePropertyNodeGen;
import ldas.duc.nodes.call.InvokeNode;
import ldas.duc.nodes.controlflow.*;
import ldas.duc.nodes.expression.*;
import ldas.duc.nodes.local.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class used by the SL {@link Parser} to create nodes. The code is factored out of the
 * automatically generated parser to keep the attributed grammar of SL small.
 */
public class NodeFactory {

    /**
     * Local variable names that are visible in the current block. Variables are not visible outside
     * of their defining block, to prevent the usage of undefined variables. Because of that, we can
     * decide during parsing if a name references a local variable or is a function name.
     */
    static class LexicalScope {
        protected final LexicalScope outer;
        protected final Map<String, FrameSlot> locals;

        LexicalScope(LexicalScope outer) {
            this.outer = outer;
            this.locals = new HashMap<>();
            if (outer != null) {
                locals.putAll(outer.locals);
            }
        }
    }

    /* State while parsing a source unit. */
    private final Source source;
    private final Map<String, RootCallTarget> allFunctions;

    /* State while parsing a function. */
    private int functionStartPos;
    private String functionName;
    private int functionBodyStartPos; // includes parameter list
    private int parameterCount;
    private FrameDescriptor frameDescriptor;
    private List<StatementNode> methodNodes;

    /* State while parsing a block. */
    private LexicalScope lexicalScope;
    private final DucLanguage language;

    public NodeFactory(DucLanguage language, Source source) {
        this.language = language;
        this.source = source;
        this.allFunctions = new HashMap<>();
    }

    public Map<String, RootCallTarget> getAllFunctions() {
        return allFunctions;
    }

    public void startFunction(Token nameToken, int bodyStartPos) {
        assert functionStartPos == 0;
        assert functionName == null;
        assert functionBodyStartPos == 0;
        assert parameterCount == 0;
        assert frameDescriptor == null;
        assert lexicalScope == null;

        functionStartPos = nameToken.charPos;
        functionName = nameToken.val;
        functionBodyStartPos = bodyStartPos;
        frameDescriptor = new FrameDescriptor();
        methodNodes = new ArrayList<>();
        startBlock();
    }

    public void addFormalParameter(Token nameToken) {
        /*
         * Method parameters are assigned to local variables at the beginning of the method. This
         * ensures that accesses to parameters are specialized the same way as local variables are
         * specialized.
         */
        final ReadArgumentNode readArg = new ReadArgumentNode(parameterCount);
        ExpressionNode assignment = createAssignment(createStringLiteral(nameToken, false), readArg);
        methodNodes.add(assignment);
        parameterCount++;
    }

    public void finishFunction(StatementNode bodyNode) {
        if (bodyNode == null) {
            // a state update that would otherwise be performed by finishBlock
            lexicalScope = lexicalScope.outer;
        } else {
            methodNodes.add(bodyNode);
            final int bodyEndPos = bodyNode.getSourceEndIndex();
            final SourceSection functionSrc = source.createSection(functionStartPos, bodyEndPos - functionStartPos);
            final StatementNode methodBlock = finishBlock(methodNodes, functionBodyStartPos, bodyEndPos - functionBodyStartPos);
            assert lexicalScope == null : "Wrong scoping of blocks in parser";

            final FunctionBodyNode functionBodyNode = new FunctionBodyNode(methodBlock);
            functionBodyNode.setSourceSection(functionSrc.getCharIndex(), functionSrc.getCharLength());

            final DucRootNode rootNode = new DucRootNode(language, frameDescriptor, functionBodyNode, functionSrc, functionName);
            allFunctions.put(functionName, Truffle.getRuntime().createCallTarget(rootNode));
        }

        functionStartPos = 0;
        functionName = null;
        functionBodyStartPos = 0;
        parameterCount = 0;
        frameDescriptor = null;
        lexicalScope = null;
    }

    public void startBlock() {
        lexicalScope = new LexicalScope(lexicalScope);
    }

    public StatementNode finishBlock(List<StatementNode> bodyNodes, int startPos, int length) {
        lexicalScope = lexicalScope.outer;

        if (containsNull(bodyNodes)) {
            return null;
        }

        List<StatementNode> flattenedNodes = new ArrayList<>(bodyNodes.size());
        flattenBlocks(bodyNodes, flattenedNodes);
        for (StatementNode statement : flattenedNodes) {
            if (statement.hasSource() && !isHaltInCondition(statement)) {
                statement.addStatementTag();
            }
        }
        BlockNode blockNode = new BlockNode(flattenedNodes.toArray(new StatementNode[flattenedNodes.size()]));
        blockNode.setSourceSection(startPos, length);
        return blockNode;
    }

    private static boolean isHaltInCondition(StatementNode statement) {
        return (statement instanceof IfNode) || (statement instanceof WhileNode);
    }

    private void flattenBlocks(Iterable<? extends StatementNode> bodyNodes, List<StatementNode> flattenedNodes) {
        for (StatementNode n : bodyNodes) {
            if (n instanceof BlockNode) {
                flattenBlocks(((BlockNode) n).getStatements(), flattenedNodes);
            } else {
                flattenedNodes.add(n);
            }
        }
    }

    /**
     * Returns an {@link DebuggerNode} for the given token.
     *
     * @param debuggerToken The token containing the debugger node's info.
     * @return A DebuggerNode for the given token.
     */
    StatementNode createDebugger(Token debuggerToken) {
        final DebuggerNode debuggerNode = new DebuggerNode();
        srcFromToken(debuggerNode, debuggerToken);
        return debuggerNode;
    }

    /**
     * Returns an {@link BreakNode} for the given token.
     *
     * @param breakToken The token containing the break node's info.
     * @return A BreakNode for the given token.
     */
    public StatementNode createBreak(Token breakToken) {
        final BreakNode breakNode = new BreakNode();
        srcFromToken(breakNode, breakToken);
        return breakNode;
    }

    /**
     * Returns an {@link ContinueNode} for the given token.
     *
     * @param continueToken The token containing the continue node's info.
     * @return A ContinueNode built using the given token.
     */
    public StatementNode createContinue(Token continueToken) {
        final ContinueNode continueNode = new ContinueNode();
        srcFromToken(continueNode, continueToken);
        return continueNode;
    }

    /**
     * Returns an {@link WhileNode} for the given parameters.
     *
     * @param whileToken The token containing the while node's info
     * @param conditionNode The conditional node for this while loop
     * @param bodyNode The body of the while loop
     * @return A WhileNode built using the given parameters. null if either conditionNode or
     *         bodyNode is null.
     */
    public StatementNode createWhile(Token whileToken, ExpressionNode conditionNode, StatementNode bodyNode) {
        if (conditionNode == null || bodyNode == null) {
            return null;
        }

        conditionNode.addStatementTag();
        final int start = whileToken.charPos;
        final int end = bodyNode.getSourceEndIndex();
        final WhileNode whileNode = new WhileNode(conditionNode, bodyNode);
        whileNode.setSourceSection(start, end - start);
        return whileNode;
    }

    /**
     * Returns an {@link IfNode} for the given parameters.
     *
     * @param ifToken The token containing the if node's info
     * @param conditionNode The condition node of this if statement
     * @param thenPartNode The then part of the if
     * @param elsePartNode The else part of the if (null if no else part)
     * @return An IfNode for the given parameters. null if either conditionNode or thenPartNode is
     *         null.
     */
    public StatementNode createIf(Token ifToken, ExpressionNode conditionNode, StatementNode thenPartNode, StatementNode elsePartNode) {
        if (conditionNode == null || thenPartNode == null) {
            return null;
        }

        conditionNode.addStatementTag();
        final int start = ifToken.charPos;
        final int end = elsePartNode == null ? thenPartNode.getSourceEndIndex() : elsePartNode.getSourceEndIndex();
        final IfNode ifNode = new IfNode(conditionNode, thenPartNode, elsePartNode);
        ifNode.setSourceSection(start, end - start);
        return ifNode;
    }

    /**
     * Returns an {@link ReturnNode} for the given parameters.
     *
     * @param t The token containing the return node's info
     * @param valueNode The value of the return (null if not returning a value)
     * @return An ReturnNode for the given parameters.
     */
    public StatementNode createReturn(Token t, ExpressionNode valueNode) {
        final int start = t.charPos;
        final int length = valueNode == null ? t.val.length() : valueNode.getSourceEndIndex() - start;
        final ReturnNode returnNode = new ReturnNode(valueNode);
        returnNode.setSourceSection(start, length);
        return returnNode;
    }

    /**
     * Returns the corresponding subclass of {@link ExpressionNode} for binary expressions. </br>
     * These nodes are currently not instrumented.
     *
     * @param opToken The operator of the binary expression
     * @param leftNode The left node of the expression
     * @param rightNode The right node of the expression
     * @return A subclass of ExpressionNode using the given parameters based on the given opToken.
     *         null if either leftNode or rightNode is null.
     */
    public ExpressionNode createBinary(Token opToken, ExpressionNode leftNode, ExpressionNode rightNode) {
        if (leftNode == null || rightNode == null) {
            return null;
        }
        final ExpressionNode leftUnboxed;
        if (leftNode instanceof BinaryNode) {  // BinaryNode never returns boxed value
            leftUnboxed = leftNode;
        } else {
            leftUnboxed = UnboxNodeGen.create(leftNode);
        }
        final ExpressionNode rightUnboxed;
        if (rightNode instanceof BinaryNode) {  // BinaryNode never returns boxed value
            rightUnboxed = rightNode;
        } else {
            rightUnboxed = UnboxNodeGen.create(rightNode);
        }

        final ExpressionNode result;
        switch (opToken.val) {
            case "+":
                result = AddNodeGen.create(leftUnboxed, rightUnboxed);
                break;
            case "*":
                result = MulNodeGen.create(leftUnboxed, rightUnboxed);
                break;
            case "/":
                result = DivNodeGen.create(leftUnboxed, rightUnboxed);
                break;
            case "-":
                result = SubNodeGen.create(leftUnboxed, rightUnboxed);
                break;
            case "<":
                result = LessThanNodeGen.create(leftUnboxed, rightUnboxed);
                break;
            case "<=":
                result = LessOrEqualNodeGen.create(leftUnboxed, rightUnboxed);
                break;
            case ">":
                result = LogicalNotNodeGen.create(LessOrEqualNodeGen.create(leftUnboxed, rightUnboxed));
                break;
            case ">=":
                result = LogicalNotNodeGen.create(LessThanNodeGen.create(leftUnboxed, rightUnboxed));
                break;
            case "==":
                result = EqualNodeGen.create(leftUnboxed, rightUnboxed);
                break;
            case "!=":
                result = LogicalNotNodeGen.create(EqualNodeGen.create(leftUnboxed, rightUnboxed));
                break;
            case "&&":
                result = new LogicalAndNode(leftUnboxed, rightUnboxed);
                break;
            case "||":
                result = new LogicalOrNode(leftUnboxed, rightUnboxed);
                break;
            default:
                throw new RuntimeException("unexpected operation: " + opToken.val);
        }

        int start = leftNode.getSourceCharIndex();
        int length = rightNode.getSourceEndIndex() - start;
        result.setSourceSection(start, length);

        return result;
    }

    /**
     * Returns an {@link InvokeNode} for the given parameters.
     *
     * @param functionNode The function being called
     * @param parameterNodes The parameters of the function call
     * @param finalToken A token used to determine the end of the sourceSelection for this call
     * @return An InvokeNode for the given parameters. null if functionNode or any of the
     *         parameterNodes are null.
     */
    public ExpressionNode createCall(ExpressionNode functionNode, List<ExpressionNode> parameterNodes, Token finalToken) {
        if (functionNode == null || containsNull(parameterNodes)) {
            return null;
        }

        final ExpressionNode result = new InvokeNode(functionNode, parameterNodes.toArray(new ExpressionNode[parameterNodes.size()]));

        final int startPos = functionNode.getSourceCharIndex();
        final int endPos = finalToken.charPos + finalToken.val.length();
        result.setSourceSection(startPos, endPos - startPos);

        return result;
    }

    /**
     * Returns an {@link WriteLocalVariableNode} for the given parameters.
     *
     * @param nameNode The name of the variable being assigned
     * @param valueNode The value to be assigned
     * @return An ExpressionNode for the given parameters. null if nameNode or valueNode is null.
     */
    public ExpressionNode createAssignment(ExpressionNode nameNode, ExpressionNode valueNode) {
        if (nameNode == null || valueNode == null) {
            return null;
        }

        String name = ((StringLiteralNode) nameNode).executeGeneric(null);
        FrameSlot frameSlot = frameDescriptor.findOrAddFrameSlot(name);
        lexicalScope.locals.put(name, frameSlot);
        final ExpressionNode result = WriteLocalVariableNodeGen.create(valueNode, frameSlot);

        if (valueNode.hasSource()) {
            final int start = nameNode.getSourceCharIndex();
            final int length = valueNode.getSourceEndIndex() - start;
            result.setSourceSection(start, length);
        }

        return result;
    }

    /**
     * Returns a {@link ReadLocalVariableNode} if this read is a local variable or a
     * {@link FunctionLiteralNode} if this read is global. In SL, the only global names are
     * functions.
     *
     * @param nameNode The name of the variable/function being read
     * @return either:
     *         <ul>
     *         <li>A ReadLocalVariableNode representing the local variable being read.</li>
     *         <li>A FunctionLiteralNode representing the function definition.</li>
     *         <li>null if nameNode is null.</li>
     *         </ul>
     */
    public ExpressionNode createRead(ExpressionNode nameNode) {
        if (nameNode == null) {
            return null;
        }

        String name = ((StringLiteralNode) nameNode).executeGeneric(null);
        final ExpressionNode result;
        final FrameSlot frameSlot = lexicalScope.locals.get(name);
        if (frameSlot != null) {
            /* Read of a local variable. */
            result = ReadLocalVariableNodeGen.create(frameSlot);
        } else {
            /* Read of a global name. In our language, the only global names are functions. */
            result = new FunctionLiteralNode(language, name);
        }
        result.setSourceSection(nameNode.getSourceCharIndex(), nameNode.getSourceLength());
        return result;
    }

    public ExpressionNode createStringLiteral(Token literalToken, boolean removeQuotes) {
        /* Remove the trailing and ending " */
        String literal = literalToken.val;
        if (removeQuotes) {
            assert literal.length() >= 2 && literal.startsWith("\"") && literal.endsWith("\"");
            literal = literal.substring(1, literal.length() - 1);
        }

        final StringLiteralNode result = new StringLiteralNode(literal.intern());
        srcFromToken(result, literalToken);
        return result;
    }

    public ExpressionNode createNumericLiteral(Token literalToken) {
        ExpressionNode result;
        try {
            /* Try if the literal is small enough to fit into a long value. */
            result = new LongLiteralNode(Long.parseLong(literalToken.val));
        } catch (NumberFormatException ex) {
            /* Overflow of long value, so fall back to BigInteger. */
            result = new BigIntegerLiteralNode(new BigInteger(literalToken.val));
        }
        srcFromToken(result, literalToken);
        return result;
    }

    public ExpressionNode createParenExpression(ExpressionNode expressionNode, int start, int length) {
        if (expressionNode == null) {
            return null;
        }

        final ParenExpressionNode result = new ParenExpressionNode(expressionNode);
        result.setSourceSection(start, length);
        return result;
    }

    /**
     * Returns an {@link ReadPropertyNode} for the given parameters.
     *
     * @param receiverNode The receiver of the property access
     * @param nameNode The name of the property being accessed
     * @return An ExpressionNode for the given parameters. null if receiverNode or nameNode is
     *         null.
     */
    public ExpressionNode createReadProperty(ExpressionNode receiverNode, ExpressionNode nameNode) {
        if (receiverNode == null || nameNode == null) {
            return null;
        }

        final ExpressionNode result = ReadPropertyNodeGen.create(receiverNode, nameNode);

        final int startPos = receiverNode.getSourceCharIndex();
        final int endPos = nameNode.getSourceEndIndex();
        result.setSourceSection(startPos, endPos - startPos);

        return result;
    }

    /**
     * Returns an {@link WritePropertyNode} for the given parameters.
     *
     * @param receiverNode The receiver object of the property assignment
     * @param nameNode The name of the property being assigned
     * @param valueNode The value to be assigned
     * @return An ExpressionNode for the given parameters. null if receiverNode, nameNode or
     *         valueNode is null.
     */
    public ExpressionNode createWriteProperty(ExpressionNode receiverNode, ExpressionNode nameNode, ExpressionNode valueNode) {
        if (receiverNode == null || nameNode == null || valueNode == null) {
            return null;
        }

        final ExpressionNode result = WritePropertyNodeGen.create(receiverNode, nameNode, valueNode);

        final int start = receiverNode.getSourceCharIndex();
        final int length = valueNode.getSourceEndIndex() - start;
        result.setSourceSection(start, length);

        return result;
    }

    /**
     * Creates source description of a single token.
     */
    private static void srcFromToken(StatementNode node, Token token) {
        node.setSourceSection(token.charPos, token.val.length());
    }

    /**
     * Checks whether a list contains a null.
     */
    private static boolean containsNull(List<?> list) {
        for (Object e : list) {
            if (e == null) {
                return true;
            }
        }
        return false;
    }

}
