package ldas.uc.sandbox.language.parser;

import ldas.uc.sandbox.language.node.LucExpressionNode;
import ldas.uc.sandbox.language.node.LucRootNode;

public class LucNodeFactory {

    public LucRootNode getRootNode() {
        return null;
    }

    public LucExpressionNode createBinary(LucExpressionNode left, Token operation, LucExpressionNode right) {
        return null;
    }

    public LucExpressionNode createIdent(Token identTk) {
        return null;
    }

    public LucExpressionNode createStringLiteral(Token stringTk) {
        return null;
    }

    public LucExpressionNode createNumLiteral(Token stringTk) {
        return null;
    }

    public LucExpressionNode createParenExpression(LucExpressionNode expr, int start, int lenght) {
        return null;
    }

    public void addFctCall(Token ident, LucExpressionNode expr) {

    }
}
