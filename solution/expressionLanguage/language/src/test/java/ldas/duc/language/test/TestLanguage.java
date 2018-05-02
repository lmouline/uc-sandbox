package ldas.duc.language.test;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Source;
import org.graalvm.polyglot.Value;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class TestLanguage {
    private static final String DUC_LANG = "duc";

    @Test
    public void testSimplePrint() throws URISyntaxException, IOException {
        File file = new File(this.getClass().getResource("/add.duc").toURI());

        Source source = Source.newBuilder(DUC_LANG, file).build();
        Context context = Context.newBuilder(DUC_LANG).in(System.in).out(System.out).build();

        Value result = context.eval(source);
        System.out.println(result);
        if (!result.isNull()) {
            System.out.println(result.toString());
        }

    }
}
