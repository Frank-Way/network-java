package models.math.functions;

import models.math.Matrix;

public abstract class Functions {
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

