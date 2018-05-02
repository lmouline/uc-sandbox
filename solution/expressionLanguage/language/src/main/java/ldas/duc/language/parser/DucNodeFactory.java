package ldas.duc.language.parser;

import ldas.duc.language.node.DucExpressionNode;
import ldas.duc.language.node.DucRootNode;

public class DucNodeFactory {

    public DucRootNode getRootNode() {
        return null;
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

    }
}
