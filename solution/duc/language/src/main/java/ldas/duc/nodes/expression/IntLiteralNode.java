package ldas.duc.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import ldas.duc.nodes.ExpressionNode;

@NodeInfo(shortName = "const")
public final class IntLiteralNode extends ExpressionNode {
    private final int value;

    public IntLiteralNode(int value) {
        this.value = value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
