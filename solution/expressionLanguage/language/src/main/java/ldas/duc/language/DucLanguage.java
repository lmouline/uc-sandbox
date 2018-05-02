package ldas.duc.language;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.TruffleObject;
import ldas.duc.language.runtime.DucContext;

@TruffleLanguage.Registration(id = DucLanguage.ID, name = "Luc", version = "0.1", mimeType = DucLanguage.MIME_TYPE)
public class DucLanguage extends TruffleLanguage<DucContext> {


    public static final String ID = "luc";
    public static final String MIME_TYPE = "application/x-sl";

    @Override
    protected DucContext createContext(Env env) {
        return new DucContext(env);
    }

    @Override
    protected boolean isObjectOfLanguage(Object object) {
        if(!(object instanceof TruffleObject)) {
            return false;
        }
        //fixme
        return true;
    }

    @Override
    protected CallTarget parse(ParsingRequest request) throws Exception {
        return super.parse(request);
    }
}
