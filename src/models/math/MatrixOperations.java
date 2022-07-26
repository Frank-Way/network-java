package models.math;

import models.math.functions.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class MatrixOperations {

    public static void assertSameShape(Matrix m1, Matrix m2) {
        if (!m1.isRowsAndColsEqual(m2)) {
            throw new IllegalArgumentException(String.format("Формы матриц (%d; %d) и (%d; %d) не совпадает",
                    m1.getRows(), m1.getCols(), m2.getRows(), m2.getCols()));
        }
    }

    public static void assertColMatrix(Matrix matrix) {
        if (matrix.getCols() > 1) {
            throw new IllegalArgumentException(String.format("Матрица размерности (%d; %d) не является столбцом",
                    matrix.getRows(), matrix.getCols()));
        }
    }

    public static void assertRowMatrix(Matrix matrix) {
        if (matrix.getRows() > 1) {
            throw new IllegalArgumentException(String.format("Матрица размерности (%d; %d) не является строкой",
                    matrix.getRows(), matrix.getCols()));
        }
    }

    public static Matrix getRandomMatrixUniform(int rows, int cols, double location, double scale) {
        return getRandomMatrix(RandomType.UNIFORM, rows, cols, location, scale);
    }

    public static Matrix getRandomMatrixUniform(int rows, int cols) {
        return getRandomMatrixUniform(rows, cols, 0.0, 1.0);
    }

    public static Matrix getRandomMatrixNormal(int rows, int cols, double location, double scale) {
        return getRandomMatrix(RandomType.NORMAL, rows, cols, location, scale);
    }

    public static Matrix getRandomMatrixNormal(int rows, int cols) {
        return getRandomMatrixNormal(rows, cols, 0.0, 1.0);
    }

    public static void shuffleMatrices(Matrix m1, Matrix m2) {
        int[] indices = getRandomIndices(m1.getRows());
        double[] tmpRow;
        int j;
        for (int i = 0; i < indices.length; i++) {
            j = indices[i];

            tmpRow = m1.getValues()[i];
            m1.getValues()[i] = m1.getValues()[j];
            m1.getValues()[j] = tmpRow;

            tmpRow = m2.getValues()[i];
            m2.getValues()[i] = m2.getValues()[j];
            m2.getValues()[j] = tmpRow;
        }
    }

    public static Matrix getLinSpace(double start, double stop, int size) {
        double[][] result = new double[size][1];
        double step = (stop - start) / (size - 1);
        for (int i = 0; i < size; i++)
            result[i][0] = start + step * i;
        return new Matrix(result);
    }

    public static int[] getLinSpace(int start, int stop, int size) {
        int[] result = new int[size];
        double step = (stop - start) * 1.0 / (size - 1);
        for (int i = 0; i < size; i++)
            result[i] = (int)Math.round(start + step * i);
        return result;
    }

    public static Matrix cartesianProduct(List<Matrix> matrices) {
        if (matrices.size() == 1)
            return matrices.get(0);
        int totalRows = matrices.stream().map(Matrix::getRows).mapToInt(Integer::intValue).reduce((a, b) -> a * b).getAsInt();
        int totalCols = matrices.stream().map(Matrix::getCols).mapToInt(Integer::intValue).sum();
        int[] extendingFactors = new int[matrices.size()];
        extendingFactors[0] = totalRows / matrices.get(0).getRows();
        for (int i = 1; i < matrices.size(); i++) {
            extendingFactors[i] = totalRows / matrices.get(i).getRows();
            for (int j = i - 1; j >= 0; j--)
                extendingFactors[i] /= extendingFactors[j];
        }
        List<Matrix> extendedMatrices = new ArrayList<>();
        for (int i = 0; i < matrices.size(); i++) {
            extendedMatrices.add(matrices.get(i).extend(extendingFactors[i], 0));
            while (extendedMatrices.get(i).getRows() < totalRows) {
                extendedMatrices.add(i, extendedMatrices.get(i).stack(extendedMatrices.get(i), 1));
                extendedMatrices.remove(i + 1);
            }
        }
        Matrix result = extendedMatrices.get(0);
        for (int i = 1; i < matrices.size(); i++)
            result = result.stack(extendedMatrices.get(i), 0);
        return result;
    }

    public static double mulScalar(Matrix m1, Matrix m2) {
        assertSameShape(m1, m2);
        assertColMatrix(m1);
        assertColMatrix(m2);
        double result = 0.0;
        for (int row = 0; row < m1.getRows(); row++)
            result += m1.getValues()[row][0] * m2.getValues()[row][0];
        return result;
    }

    private static int[] getRandomIndices(int length) {
        List<Integer> list = IntStream.range(0, length).boxed().collect(Collectors.toList());
        Collections.shuffle(list);
        return list.stream().mapToInt(i -> i).toArray();
    }

    private static Matrix getRandomMatrix(RandomType type, int rows, int cols, double location, double scale) {
        Random random = new Random();
        double[][] result = new double[rows][cols];
        double randomValue = 0.0;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++) {
                switch (type) {
                    case UNIFORM:
                        randomValue = random.nextDouble();
                        break;
                    case NORMAL:
                        randomValue = random.nextGaussian();
                        break;
                }
                result[row][col] = randomValue * scale + location;
            }
        return new Matrix(result);
    }

    public abstract static class Functions {
        private static Abs absOperation = new Abs();
        private static Exp expOperation = new Exp();
        private static Tanh tanhOperation = new Tanh();
        private static Pow powOperation;

        public static Matrix exp(Matrix matrix) {
            return applyToEachCell(matrix, expOperation);
        }

        public static Matrix tanh(Matrix matrix) {
            return applyToEachCell(matrix, tanhOperation);
        }

        public static Matrix abs(Matrix matrix) {
            return applyToEachCell(matrix, absOperation);
        }

        public static Matrix pow(Matrix matrix, double scale) {
            powOperation = new Pow(scale);
            return applyToEachCell(matrix, powOperation);
        }

        private static Matrix applyToEachCell(Matrix matrix, DoubleOperation operation) {
            double[][] result = new double[matrix.getRows()][matrix.getCols()];
            for (int row = 0; row < matrix.getRows(); row++)
                for (int col = 0; col < matrix.getCols(); col++)
                    result[row][col] = operation.apply(matrix.getValues()[row][col]);
            return new Matrix(result);
        }
    }
}
