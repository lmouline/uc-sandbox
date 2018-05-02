package ldas.duc.language.node;


import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import ldas.duc.language.DucLanguage;

@NodeInfo(language = "Luc", description = "Root of Luc AST")
public class DucRootNode extends RootNode {

    public DucRootNode(DucLanguage language, FrameDescriptor frameDescriptor) {
        super(language, frameDescriptor);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return null;
    }
}
