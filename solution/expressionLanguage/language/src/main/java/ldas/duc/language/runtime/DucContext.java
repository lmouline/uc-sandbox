package ldas.duc.language.runtime;

import com.oracle.truffle.api.TruffleLanguage;

import java.util.HashMap;
import java.util.Map;

public class DucContext {
//    private final PrintWriter output;
//    private final BufferedReader input;

    private final Map<String, DucFunction> functions;


    public DucContext(TruffleLanguage.Env env) {
//        this.input = new BufferedReader(new InputStreamReader(env.in()));
//        this.output = new PrintWriter(env.out(), true);
        functions = new HashMap<>();



    }

    public static Object fromForeignValue(Object argument) {
        //todo fixme
        return argument;
    }
}
