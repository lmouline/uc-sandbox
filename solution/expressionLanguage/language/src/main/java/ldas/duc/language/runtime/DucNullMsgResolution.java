package ldas.duc.language.runtime;

import com.oracle.truffle.api.interop.CanResolve;
import com.oracle.truffle.api.interop.MessageResolution;
import com.oracle.truffle.api.interop.Resolve;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.nodes.Node;

@MessageResolution(receiverType = DucNull.class)
public class DucNullMsgResolution {

    @Resolve(message = "IS_NULL")
    public abstract static class DucForeignIsNullNode extends Node {

        public Object access(Object receiver) {
            return DucNull.SINGLETON == receiver;
        }
    }

    @CanResolve
    public abstract static class CheckNull extends Node {
        protected static boolean test(TruffleObject receiver) {
            return receiver instanceof DucNull;
        }
    }
}
