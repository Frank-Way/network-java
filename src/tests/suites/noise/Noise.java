package tests.suites.noise;

import tests.cases.CaseWrapper;
import tests.cases.noise.ExtendingNoise;
import tests.cases.noise.NoNoise;
import tests.cases.noise.ReplacingNoise;
import tests.suites.Suite;

import java.util.Date;

public class Noise extends Suite {
    @Override
    public CaseWrapper[] getCases() {
        return new CaseWrapper[] {
                new NoNoise(),
                new ExtendingNoise(),
                new ReplacingNoise(),
        };
    }

    @Override
    public Object[][] getArgs() {
        return new Object[][] {
                {},
                {},
                {},
        };
    }

    @Override
    public String getId() {
        return getClass().getCanonicalName();
    }

    @Override
    public String getDescription() {
        return "Проверка зашумления выборки";
    }
}
