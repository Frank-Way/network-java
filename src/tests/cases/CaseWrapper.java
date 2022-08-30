package tests.cases;

import tests.TestStatus;

public abstract class CaseWrapper {
    public abstract String getId();
    public abstract String getDescription();
    public abstract TestStatus process(Object ... args);
}
