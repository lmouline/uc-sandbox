package ldas.uc.sandbox;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.io.IOException;

public class LucLauncher {


    private static final String LUC_LANG = "luc";


    public static void main(String[] args) throws IOException {
        if(args.length != 1 ) {
            System.err.println("No file set as input.");
        }
        String file = args[0];
        Source source = Source.newBuilder(LUC_LANG, new File(file)).build();
        Context context = Context.newBuilder(LUC_LANG).in(System.in).out(System.out).build();

        try {
            Value result = context.eval(source);
            if (!result.isNull()) {
                System.out.println(result.toString());
            }
        } catch (PolyglotException e) {
            e.printStackTrace();
        } finally {
            context.close();
        }
    }
}
