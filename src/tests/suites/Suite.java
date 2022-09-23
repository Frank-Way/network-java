package tests.suites;

import tests.cases.CaseWrapper;

public abstract class Suite {
    public abstract CaseWrapper[] getCases();

    public abstract Object[][] getArgs();

    public abstract String getId();

    public abstract String getDescription();

    @Override
    public String toString() {
        return "Suite{" +
                "id=" + getId() +
                ", description=\"" + getDescription() + "\"" +
                "}";
    }
}
