package ldas.duc.language.node;

import com.oracle.truffle.api.CompilerDirectives;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.frame.VirtualFrame;
import ldas.duc.language.DucLanguage;
import ldas.duc.language.runtime.DucContext;
import org.graalvm.compiler.nodeinfo.NodeInfo;

import java.io.PrintWriter;

@NodeInfo(shortName = "function call")
public class FunctionCallNode extends DucStatementNode {


    @Specialization
    public void println(Object value) {
        DucContext ctx = getRootNode().getLanguage(DucLanguage.class).getContextReference().get();
        println(ctx.out(), value);
        return null;
    }

    @CompilerDirectives.TruffleBoundary
    private void println(PrintWriter out, Object value) {
        out.print(value + "\n");
    }

    @Override
    public Object execute(VirtualFrame frame) {
        return null;
    }
}
