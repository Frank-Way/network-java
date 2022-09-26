package tests.cases.coding.binary;

import models.data.approximation.binary.BinaryCoder;
import tests.TestStatus;

import java.util.Arrays;
import java.util.Random;

public class DoubleBinaryCoding extends BaseForBinaryCoding{
    @Override
    public String getDescription() {
        return "Проверка кодирования вещественных чисел";
    }

    @Override
    public TestStatus process(Object... args) {
        final BinaryCoder binaryCoder = new BinaryCoder();
        final int testCasesCount = (int) args[0];
        final double maxMagnitude = (double) args[1];
        final int[] reduceFactors = (int[]) args[2];
        final double[] testCases = new double[testCasesCount];
        Random random = new Random();
        for (int i = 0; i < testCasesCount; i++)
            testCases[i] = random.nextDouble() * 2 * maxMagnitude - maxMagnitude;

//        double[][] absErrors = new double[reduceFactors.length][testCasesCount];
        final double[] avgAbsErrors = new double[reduceFactors.length];
        final double[] maxAbsErrors = new double[reduceFactors.length];

//        double[][] relErrors = new double[reduceFactors.length][testCasesCount];
        final double[] avgRelErrors = new double[reduceFactors.length];
        final double[] maxRelErrors = new double[reduceFactors.length];

        for (int i = 0; i < reduceFactors.length; i++ ) {
            maxAbsErrors[i] = 0.0;
            maxRelErrors[i] = 0.0;
            int reduceFactor = reduceFactors[i];
            for (int j = 0; j < testCasesCount; j++) {
                double absError = Math.abs(testCases[j] - binaryCoder.decodeDouble(binaryCoder.code(testCases[j], reduceFactor)));
                if (i == 0 && absError > 0.0)
                    System.out.println(testCases[j]);
//                absErrors[i][j] = absError;
                avgAbsErrors[i] = avgAbsErrors[i] + absError / testCasesCount;
                maxAbsErrors[i] = Math.max(maxAbsErrors[i], absError);
                double relError = Math.abs(absError / testCases[j]);
//                relErrors[i][j] = relError;
                avgRelErrors[i] = avgRelErrors[i] + relError / testCasesCount;
                maxRelErrors[i] = Math.max(maxRelErrors[i], relError);
            }
        }
        System.out.println("Средние ошибки");
        System.out.println("Абсолютные ошибки:    " + Arrays.toString(avgAbsErrors));
        System.out.println("Относительные ошибки: " + Arrays.toString(avgRelErrors));
        System.out.println("Максимальные ошибки");
        System.out.println("Абсолютные ошибки:    " + Arrays.toString(maxAbsErrors));
        System.out.println("Относительные ошибки: " + Arrays.toString(maxRelErrors));
        return TestStatus.PASSED;
    }
}
