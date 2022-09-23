package tests.cases.noise;

import tests.cases.CaseWrapper;

public abstract class BaseForNoise extends CaseWrapper {
    @Override
    public String getId() {
        return getClass().getCanonicalName();
    }
}
