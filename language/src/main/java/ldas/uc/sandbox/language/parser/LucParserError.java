package ldas.uc.sandbox.language.parser;

import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;

public class LucParserError extends RuntimeException implements TruffleException{

    public LucParserError(String message) {
        super(message);
    }

    @Override
    public Node getLocation() {
        return null;
    }

}
