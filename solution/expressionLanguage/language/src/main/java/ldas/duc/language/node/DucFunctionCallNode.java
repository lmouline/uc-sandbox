package ldas.duc.language.node;

import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.frame.VirtualFrame;
import ldas.duc.language.runtime.DucContext;

public class DucFunctionCallNode extends DucStatementNode{
    private final String functionName;
    private final DucExpressionNode paramter;
    private final TruffleLanguage.ContextReference<DucContext> context;


    public DucFunctionCallNode(TruffleLanguage.ContextReference<DucContext> context, String functionName, DucExpressionNode paramter) {
        this.functionName = functionName;
        this.paramter = paramter;
        this.context = context;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        context.get().


        return null;
    }
}
