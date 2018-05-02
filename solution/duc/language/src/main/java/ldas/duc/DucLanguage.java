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
package ldas.duc;

import com.oracle.truffle.api.*;
import com.oracle.truffle.api.debug.DebuggerTags;
import com.oracle.truffle.api.dsl.NodeFactory;
import com.oracle.truffle.api.frame.Frame;
import com.oracle.truffle.api.instrumentation.ProvidedTags;
import com.oracle.truffle.api.instrumentation.StandardTags;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.object.DynamicObject;
import com.oracle.truffle.api.source.Source;
import com.oracle.truffle.api.source.SourceSection;
import ldas.duc.builtins.BuiltinNode;
import ldas.duc.nodes.EvalRootNode;
import ldas.duc.nodes.Types;
import ldas.duc.nodes.access.ReadPropertyCacheNode;
import ldas.duc.nodes.access.ReadPropertyNode;
import ldas.duc.nodes.access.WritePropertyCacheNode;
import ldas.duc.nodes.access.WritePropertyNode;
import ldas.duc.nodes.call.DispatchNode;
import ldas.duc.nodes.call.InvokeNode;
import ldas.duc.nodes.controlflow.*;
import ldas.duc.nodes.expression.*;
import ldas.duc.nodes.local.LexicalScope;
import ldas.duc.nodes.local.ReadLocalVariableNode;
import ldas.duc.nodes.local.WriteLocalVariableNode;
import ldas.duc.parser.Parser;
import ldas.duc.runtime.BigNumber;
import ldas.duc.runtime.Context;
import ldas.duc.runtime.Function;
import ldas.duc.runtime.Null;

import java.util.*;

/**
 * SL is a simple language to demonstrate and showcase features of Truffle. The implementation is as
 * simple and clean as possible in order to help understanding the ideas and concepts of Truffle.
 * The language has first class functions, and objects are key-value stores.
 * <p>
 * SL is dynamically typed, i.e., there are no type names specified by the programmer. SL is
 * strongly typed, i.e., there is no automatic conversion between types. If an operation is not
 * available for the types encountered at run time, a type error is reported and execution is
 * stopped. For example, {@code 4 - "2"} results in a type error because subtraction is only defined
 * for numbers.
 *
 * <p>
 * <b>Types:</b>
 * <ul>
 * <li>Number: arbitrary precision integer numbers. The implementation uses the Java primitive type
 * {@code long} to represent numbers that fit into the 64 bit range, and {@link BigNumber} for
 * numbers that exceed the range. Using a primitive type such as {@code long} is crucial for
 * performance.
 * <li>Boolean: implemented as the Java primitive type {@code boolean}.
 * <li>String: implemented as the Java standard type {@link String}.
 * <li>Function: implementation type {@link Function}.
 * <li>Object: efficient implementation using the object model provided by Truffle. The
 * implementation type of objects is a subclass of {@link DynamicObject}.
 * <li>Null (with only one value {@code null}): implemented as the singleton
 * {@link Null#SINGLETON}.
 * </ul>
 * The class {@link Types} lists these types for the Truffle DSL, i.e., for type-specialized
 * operations that are specified using Truffle DSL annotations.
 *
 * <p>
 * <b>Language concepts:</b>
 * <ul>
 * <li>Literals for {@link BigIntegerLiteralNode numbers} , {@link StringLiteralNode strings},
 * and {@link FunctionLiteralNode functions}.
 * <li>Basic arithmetic, logical, and comparison operations: {@link AddNode +}, {@link SubNode
 * -}, {@link MulNode *}, {@link DivNode /}, {@link LogicalAndNode logical and},
 * {@link LogicalOrNode logical or}, {@link EqualNode ==}, !=, {@link LessThanNode &lt;},
 * {@link LessOrEqualNode &le;}, &gt;, &ge;.
 * <li>Local variables: local variables must be defined (via a {@link WriteLocalVariableNode
 * write}) before they can be used (by a {@link ReadLocalVariableNode read}). Local variables are
 * not visible outside of the block where they were first defined.
 * <li>Basic control flow statements: {@link BlockNode blocks}, {@link IfNode if},
 * {@link WhileNode while} with {@link BreakNode break} and {@link ContinueNode continue},
 * {@link ReturnNode return}.
 * <li>Debugging control: {@link DebuggerNode debugger} statement uses
 * {@link DebuggerTags#AlwaysHalt} tag to halt the execution when run under the debugger.
 * <li>Function calls: {@link InvokeNode invocations} are efficiently implemented with
 * {@link DispatchNode polymorphic inline caches}.
 * <li>Object access: {@link ReadPropertyNode} uses {@link ReadPropertyCacheNode} as the
 * polymorphic inline cache for property reads. {@link WritePropertyNode} uses
 * {@link WritePropertyCacheNode} as the polymorphic inline cache for property writes.
 * </ul>
 *
 * <p>
 * <b>Syntax and parsing:</b><br>
 * The syntax is described as an attributed grammar. The {@link Parser} and {@link Scanner} are
 * automatically generated by the parser generator Coco/R (available from
 * <a href="http://ssw.jku.at/coco/">http://ssw.jku.at/coco/</a>). The grammar contains semantic
 * actions that build the AST for a method. To keep these semantic actions short, they are mostly
 * calls to the {@link SLNodeFactory} that performs the actual node creation. All functions found in
 * the SL source are added to the {@link SLFunctionRegistry}, which is accessible from the
 * {@link Context}.
 *
 * <p>
 * <b>Builtin functions:</b><br>
 * Library functions that are available to every SL source without prior definition are called
 * builtin functions. They are added to the {@link SLFunctionRegistry} when the {@link Context} is
 * created. Some of the current builtin functions are
 * <ul>
 * <li>{@link SLReadlnBuiltin readln}: Read a String from the {@link Context#getInput() standard
 * input}.
 * <li>{@link SLPrintlnBuiltin println}: Write a value to the {@link Context#getOutput() standard
 * output}.
 * <li>{@link SLNanoTimeBuiltin nanoTime}: Returns the value of a high-resolution time, in
 * nanoseconds.
 * <li>{@link SLDefineFunctionBuiltin defineFunction}: Parses the functions provided as a String
 * argument and adds them to the function registry. Functions that are already defined are replaced
 * with the new version.
 * <li>{@link SLStackTraceBuiltin stckTrace}: Print all function activations with all local
 * variables.
 * </ul>
 */
@TruffleLanguage.Registration(id = DucLanguage.ID, name = "SL", version = "0.30", mimeType = DucLanguage.MIME_TYPE)
@ProvidedTags({StandardTags.CallTag.class, StandardTags.StatementTag.class, StandardTags.RootTag.class, StandardTags.ExpressionTag.class, DebuggerTags.AlwaysHalt.class})
public final class DucLanguage extends TruffleLanguage<Context> {
    public static volatile int counter;

    public static final String ID = "sl";
    public static final String MIME_TYPE = "application/x-sl";

    public DucLanguage() {
        counter++;
    }

    @Override
    protected Context createContext(Env env) {
        return new Context(this, env, new ArrayList<>(EXTERNAL_BUILTINS));
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        Source source = request.getSource();
        Map<String, RootCallTarget> functions;
        /*
         * Parse the provided source. At this point, we do not have a Context yet. Registration of
         * the functions with the Context happens lazily in EvalRootNode.
         */
        if (request.getArgumentNames().isEmpty()) {
            functions = Parser.parseSL(this, source);
        } else {
            Source requestedSource = request.getSource();
            StringBuilder sb = new StringBuilder();
            sb.append("function main(");
            String sep = "";
            for (String argumentName : request.getArgumentNames()) {
                sb.append(sep);
                sb.append(argumentName);
                sep = ",";
            }
            sb.append(") { return ");
            sb.append(request.getSource().getCharacters());
            sb.append(";}");
            String language = requestedSource.getLanguage() == null ? ID : requestedSource.getLanguage();
            String mimeType = requestedSource.getMimeType() == null ? MIME_TYPE : requestedSource.getMimeType();

            Source decoratedSource = Source.newBuilder(sb.toString()).language(language).mimeType(mimeType).name(
                            request.getSource().getName()).build();
            functions = Parser.parseSL(this, decoratedSource);
        }

        RootCallTarget main = functions.get("main");
        RootNode evalMain;
        if (main != null) {
            /*
             * We have a main function, so "evaluating" the parsed source means invoking that main
             * function. However, we need to lazily register functions into the Context first, so
             * we cannot use the original DucRootNode for the main function. Instead, we create a new
             * EvalRootNode that does everything we need.
             */
            evalMain = new EvalRootNode(this, main, functions);
        } else {
            /*
             * Even without a main function, "evaluating" the parsed source needs to register the
             * functions into the Context.
             */
            evalMain = new EvalRootNode(this, null, functions);
        }
        return Truffle.getRuntime().createCallTarget(evalMain);
    }

    /*
     * Still necessary for the old SL TCK to pass. We should remove with the old TCK. New language
     * should not override this.
     */
    @SuppressWarnings("deprecation")
    @Override
    protected Object findExportedSymbol(Context context, String globalName, boolean onlyExplicit) {
        return context.getFunctionRegistry().lookup(globalName, false);
    }

    @Override
    protected boolean isVisible(Context context, Object value) {
        return value != Null.SINGLETON;
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        if (!(object instanceof TruffleObject)) {
            return false;
        }
        TruffleObject truffleObject = (TruffleObject) object;
        return truffleObject instanceof Function || truffleObject instanceof BigNumber || Context.isSLObject(truffleObject);
    }

    @Override
    protected String toString(Context context, Object value) {
        if (value == Null.SINGLETON) {
            return "NULL";
        }
        if (value instanceof BigNumber) {
            return super.toString(context, ((BigNumber) value).getValue());
        }
        if (value instanceof Long) {
            return Long.toString((Long) value);
        }
        return super.toString(context, value);
    }

    @Override
    protected Object findMetaObject(Context context, Object value) {
        if (value instanceof Number || value instanceof BigNumber) {
            return "Number";
        }
        if (value instanceof Boolean) {
            return "Boolean";
        }
        if (value instanceof String) {
            return "String";
        }
        if (value == Null.SINGLETON) {
            return "Null";
        }
        if (value instanceof Function) {
            return "Function";
        }
        return "Object";
    }

    @Override
    protected SourceSection findSourceLocation(Context context, Object value) {
        if (value instanceof Function) {
            Function f = (Function) value;
            return f.getCallTarget().getRootNode().getSourceSection();
        }
        return null;
    }

    @Override
    public Iterable<Scope> findLocalScopes(Context context, Node node, Frame frame) {
        final LexicalScope scope = LexicalScope.createScope(node);
        return new Iterable<Scope>() {
            @Override
            public Iterator<Scope> iterator() {
                return new Iterator<Scope>() {
                    private LexicalScope previousScope;
                    private LexicalScope nextScope = scope;

                    @Override
                    public boolean hasNext() {
                        if (nextScope == null) {
                            nextScope = previousScope.findParent();
                        }
                        return nextScope != null;
                    }

                    @Override
                    public Scope next() {
                        if (!hasNext()) {
                            throw new NoSuchElementException();
                        }
                        Scope vscope = Scope.newBuilder(nextScope.getName(), nextScope.getVariables(frame)).node(nextScope.getNode()).arguments(nextScope.getArguments(frame)).build();
                        previousScope = nextScope;
                        nextScope = null;
                        return vscope;
                    }
                };
            }
        };
    }

    @Override
    protected Iterable<Scope> findTopScopes(Context context) {
        return context.getTopScopes();
    }

    public static Context getCurrentContext() {
        return getCurrentContext(DucLanguage.class);
    }

    private static final List<NodeFactory<? extends BuiltinNode>> EXTERNAL_BUILTINS = Collections.synchronizedList(new ArrayList<>());

    public static void installBuiltin(NodeFactory<? extends BuiltinNode> builtin) {
        EXTERNAL_BUILTINS.add(builtin);
    }

}
