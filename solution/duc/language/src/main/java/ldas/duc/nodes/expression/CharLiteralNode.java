package ldas.duc.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import ldas.duc.nodes.ExpressionNode;

@NodeInfo(shortName = "const")
public final class CharLiteralNode extends ExpressionNode {

    private final char value;

    public CharLiteralNode(char value) {
        this.value = value;
    }

    @Override
    public Character executeGeneric(VirtualFrame frame) {
        return this.value;
    }
}
