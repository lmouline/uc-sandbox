package ldas.duc.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import ldas.duc.nodes.ExpressionNode;

@NodeInfo(shortName = "const")
public class BoolLiteralNode extends ExpressionNode {

    private final boolean value;

    public BoolLiteralNode(boolean value) {
        this.value = value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
