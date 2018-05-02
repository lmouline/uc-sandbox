package ldas.duc.language.node;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;

public abstract class DucStatementNode extends Node {


    public abstract Object execute(VirtualFrame frame);


}
