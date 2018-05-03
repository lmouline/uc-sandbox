package ldas.duc.nodes.local;

import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeField;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.FrameSlot;
import com.oracle.truffle.api.frame.FrameSlotKind;
import com.oracle.truffle.api.frame.VirtualFrame;
import ldas.duc.nodes.ExpressionNode;

@NodeChild("dclNode")
@NodeField(name = "slot", type = FrameSlot.class)
public abstract class DeclareLocalVariableNode extends ExpressionNode {

    protected abstract FrameSlot getSlot();

    @Specialization(guards = "isLong(frame)")
    protected long declareLong(VirtualFrame frame) {
//        getSlot().setKind(FrameSlotKind.Long);
        frame.setLong(getSlot(), 0L);
        return 0L;
    }

    @Specialization(guards = "isDouble(frame)")
    protected double declareDouble(VirtualFrame frame) {
//        getSlot().setKind(FrameSlotKind.Double);
        frame.setDouble(getSlot(), 0.);
        return 0.;
    }

    @Specialization(guards = "isInt(frame)")
    protected int declareInt(VirtualFrame frame) {
//        getSlot().setKind(FrameSlotKind.Int);
        frame.setInt(getSlot(), 0);
        return 0;
    }

    @Specialization(guards = "isBool(frame)")
    protected boolean declareBool(VirtualFrame frame) {
//        getSlot().setKind(FrameSlotKind.Boolean);
        frame.setBoolean(getSlot(), false);
        return false;
    }

    @Specialization(replaces = {"declareLong", "declareDouble", "declareInt", "declareBool"})
    protected Object declare(VirtualFrame frame) {
        getSlot().setKind(FrameSlotKind.Object);
        frame.setObject(getSlot(), null);
        return null;
    }

    protected boolean isLong(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Long;
    }

    protected boolean isDouble(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Double;
    }

    protected boolean isInt(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Int;
    }

    protected boolean isBool(VirtualFrame frame) {
        return getSlot().getKind() == FrameSlotKind.Boolean;
    }
}
