package ldas.duc.language.parser;

import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import ldas.duc.language.DucLanguage;
import ldas.duc.language.node.DucExpressionNode;
import ldas.duc.language.node.DucFunctionCallNode;
import ldas.duc.language.node.DucRootNode;

public class DucASTFactory {

    private RootCallTarget firstStatement;
    private final DucLanguage language;

    public DucASTFactory(DucLanguage language) {
        this.language = language;
    }


    public RootCallTarget getRootNode() {
        return this.firstStatement;
    }

    public DucExpressionNode createBinary(DucExpressionNode left, Token operation, DucExpressionNode right) {
        return null;
    }

    public DucExpressionNode createIdent(Token identTk) {
        return null;
    }

    public DucExpressionNode createStringLiteral(Token stringTk) {
        return null;
    }

    public DucExpressionNode createNumLiteral(Token stringTk) {
        return null;
    }

    public DucExpressionNode createParenExpression(DucExpressionNode expr, int start, int lenght) {
        return null;
    }

    public void addFctCall(Token ident, DucExpressionNode expr) {
        if(this.firstStatement == null) {
            final DucFunctionCallNode fctCall = new DucFunctionCallNode();
            final FrameDescriptor frameDescriptor = new FrameDescriptor(); //fixme why?
            final DucRootNode rootNode = new DucRootNode(this.language, frameDescriptor, fctCall);
            this.firstStatement = Truffle.getRuntime().createCallTarget(rootNode);
        }
    }
}
