package ldas.duc.language.runtime;

import com.oracle.truffle.api.RootCallTarget;

public class DucFunction {

    private final String name;
    private final RootCallTarget fctBody;

    public DucFunction(String name, RootCallTarget fctBody) {
        this.name = name;
        this.fctBody = fctBody;
    }

    public String getName() {
        return name;
    }

    public RootCallTarget getFctBody() {
        return fctBody;
    }
}
