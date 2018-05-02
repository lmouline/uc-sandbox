package ldas.duc.language;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.interop.TruffleObject;
import com.oracle.truffle.api.source.Source;
import ldas.duc.language.node.DucRootNode;
import ldas.duc.language.parser.Parser;
import ldas.duc.language.runtime.DucContext;


@TruffleLanguage.Registration(id = DucLanguage.ID, name = "Duc", version = "0.1", mimeType = DucLanguage.MIME_TYPE)
public class DucLanguage extends TruffleLanguage<DucContext> {


    public static final String ID = "duc";
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

        Source source = request.getSource();
        DucRootNode rootNode = Parser.parseDuc(this, source);

        return Truffle.getRuntime().createCallTarget(rootNode);
    }
}
