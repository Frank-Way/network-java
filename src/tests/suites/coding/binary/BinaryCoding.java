package tests.suites.coding.binary;

import tests.cases.CaseWrapper;
import tests.cases.coding.binary.DoubleBinaryCoding;
import tests.cases.coding.binary.MatrixBinaryCoding;
import tests.suites.Suite;

import java.util.Date;

public class BinaryCoding extends Suite {
    @Override
    public CaseWrapper[] getCases() {
        return new CaseWrapper[] {
                new DoubleBinaryCoding(),
                new MatrixBinaryCoding(),
        };
    }

    @Override
    public Object[][] getArgs() {
        final int[] reducingFactors = new int[] {1, 2, 4};
        return new Object[][] {
                {100_000, 100_000.0, reducingFactors},
                {5, 10, 10, 0.01, reducingFactors},
        };
    }

    @Override
    public String getId() {
        return getClass().getCanonicalName();
    }

    @Override
    public String getDescription() {
        return "Проверка бинарного кодирования";
    }
}
