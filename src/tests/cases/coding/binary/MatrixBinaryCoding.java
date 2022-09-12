package tests.cases.coding.binary;

import models.data.approximation.binary.BinaryCoder;
import models.data.approximation.binary.BinaryMatrix;
import models.math.Matrix;
import models.math.MatrixUtils;
import tests.TestStatus;

public class MatrixBinaryCoding extends BaseForBinaryCoding{
    @Override
    public String getDescription() {
        return "Проверка кодирования матриц";
    }

    @Override
    public TestStatus process(Object... args) {
        System.out.println(getId() + " [" + getDescription() + "]");
        final BinaryCoder binaryCoder = new BinaryCoder();

        final int repeats = (int) args[0];
        final int rows = (int) args[1];
        final int cols = (int) args[2];
        final double maxDelta = (double) args[3];
        final int[] reduceFactors = (int[]) args[4];

        for (int repeat = 0; repeat < repeats; repeat++)
            for (int reducingFactor: reduceFactors) {
                final Matrix randomMatrix = MatrixUtils.getRandomMatrixUniform(rows, cols);
                final BinaryMatrix codedMatrix = binaryCoder.codeMatrix(randomMatrix, reducingFactor);
                final Matrix decodedMatrix = binaryCoder.decode(codedMatrix);
                final double delta = randomMatrix.sub(decodedMatrix).abs().max();
                System.out.printf("delta = %f%n", delta);
                if (maxDelta < delta) {
                    System.out.printf("maxDelta (%f) < delta (%f)%n", maxDelta, delta);
                    return TestStatus.FAILED;
                }
            }
        return TestStatus.PASSED;
    }
}
