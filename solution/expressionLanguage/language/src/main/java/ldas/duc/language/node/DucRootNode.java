package ldas.duc.language.node;


import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.nodes.RootNode;
import ldas.duc.language.DucLanguage;

/**
 * Root node of the DUC Language
 *
 * It executed the first expression in the DUC script
 *
 */
@NodeInfo(language = "Duc", description = "Root of Duc AST")
public class DucRootNode extends RootNode {

    @Child
    private DucStatementNode firstStmt;


    public DucRootNode(DucLanguage language, FrameDescriptor frameDescriptor, DucStatementNode stmt) {
        super(language, frameDescriptor);
        this.firstStmt = stmt;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        assert getLanguage(DucLanguage.class).getContextReference().get() != null;
        return firstStmt.execute(frame);
    }
}
