package tests.cases.coding;

import tests.cases.CaseWrapper;

public abstract class BaseForCoding extends CaseWrapper {
    @Override
    public String getId() {
        return getClass().getCanonicalName();
    }
}
