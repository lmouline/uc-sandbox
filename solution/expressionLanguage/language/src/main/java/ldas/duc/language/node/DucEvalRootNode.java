package ldas.duc.language.node;


import com.oracle.truffle.api.RootCallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.DirectCallNode;
import com.oracle.truffle.api.nodes.RootNode;
import ldas.duc.language.runtime.DucNull;

/**
 * Root node of the DUC Language
 *
 * It executed the first expression in the DUC script
 *
 */
//fixme merge with DucRootNode ??
public class DucEvalRootNode extends RootNode {

    @Child
    private DirectCallNode firstExpr;


    public DucEvalRootNode(RootCallTarget firstExpr) {
        super(null);
        this.firstExpr = (firstExpr != null)? DirectCallNode.create(firstExpr) : null;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        if(firstExpr == null) {
            return DucNull.SINGLETON;
        }

        Object[] arguments = frame.getArguments();
        //fixme
//        for (int i = 0; i < arguments.length; i++) {
//            arguments[i] = DucContext.fromForeignValue(arguments[i]);
//        }

        return firstExpr.call(arguments);
    }
}
