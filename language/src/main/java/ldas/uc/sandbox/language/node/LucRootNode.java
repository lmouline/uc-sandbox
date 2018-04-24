package ldas.uc.sandbox.language.node;


import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import ldas.uc.sandbox.language.LucLanguage;

@NodeInfo(language = "Luc", description = "Root of Luc AST")
public class LucRootNode extends RootNode {

    public LucRootNode(LucLanguage language, FrameDescriptor frameDescriptor) {
        super(language, frameDescriptor);
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return null;
    }
}
