package tests.suites.coding.binary;

import tests.cases.CaseWrapper;
import tests.cases.coding.binary.DoubleBinaryCoding;
import tests.cases.coding.binary.MatrixBinaryCoding;

import java.util.Date;

public class BinaryCoding {
    public static void main(String[] args) {
        final int[] reducingFactors = new int[] {1, 2, 4};
        CaseWrapper[] tests = new CaseWrapper[] {
                new DoubleBinaryCoding(),
                new MatrixBinaryCoding(),
        };
        Object[][] testArgs = new Object[][] {
                {100_000, 100_000.0, reducingFactors},
                {5, 10, 10, 0.01, reducingFactors}
        };
        for (int i = 0; i < tests.length; i++) {
            CaseWrapper test = tests[i];
            try {
                test.process(testArgs[i]);
            } catch (Exception e) {
                System.out.println(new Date() + "    " + "Неожиданное исключение во время выполнения: " + test.getId());
            }
        }
    }
}
