package ldas.duc.nodes.expression;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import ldas.duc.nodes.ExpressionNode;

@NodeInfo(shortName = "const")
public class DoubleLiteralNode extends ExpressionNode {

    private final double value;

    public DoubleLiteralNode(double value) {
        this.value = value;
    }

    @Override
    public Object executeGeneric(VirtualFrame frame) {
        return value;
    }
}
