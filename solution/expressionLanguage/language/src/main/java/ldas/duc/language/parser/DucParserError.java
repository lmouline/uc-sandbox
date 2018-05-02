package ldas.duc.language.parser;

import com.oracle.truffle.api.TruffleException;
import com.oracle.truffle.api.nodes.Node;

public class DucParserError extends RuntimeException implements TruffleException{

    public DucParserError(String message) {
        super(message);
    }

    @Override
    public Node getLocation() {
        return null;
    }

}
