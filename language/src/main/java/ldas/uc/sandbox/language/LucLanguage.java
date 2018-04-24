package ldas.uc.sandbox.language;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.TruffleObject;
import ldas.uc.sandbox.language.runtime.LucContext;

@TruffleLanguage.Registration(id = LucLanguage.ID, name = "Luc", version = "0.1", mimeType = LucLanguage.MIME_TYPE)
public class LucLanguage extends TruffleLanguage<LucContext> {


    public static final String ID = "luc";
    public static final String MIME_TYPE = "application/x-sl";

    @Override
    protected LucContext createContext(Env env) {
        return new LucContext(env);
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
