package ldas.duc.language.runtime;

import com.oracle.truffle.api.interop.ForeignAccess;
import com.oracle.truffle.api.interop.TruffleObject;

public class DucNull implements TruffleObject {

    public static final DucNull SINGLETON = new DucNull();

    private DucNull(){}

    @Override
    public ForeignAccess getForeignAccess() {
        return DucNullMsgResolutionForeign.ACCESS;
    }

    @Override
    public String toString() {
        return "null";
    }
}
