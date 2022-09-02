package models.math.functions;

import models.math.Matrix;

/**
 * Набор функций, применяемых к каждому элементу матрицы. Доступные операции:
 * <pre><ul>
 * <li>abs - f(x) = |x| (получение абсолютных значений);</li>
 * <li>exp - f(x) = exp(x) (экспонента);</li>
 * <li>pow - f(x, scale) = x ^ scale (возведение в степень);</li>
 * <li>tanh - f(x) = tanh(x) (гиперболический тангенс).</li>
 * </ul></pre>
 */
public abstract class MatrixFunctions {
    private static Abs absOperation = new Abs();
    private static Exp expOperation = new Exp();
    private static Tanh tanhOperation = new Tanh();
    private static Pow powOperation;

    /**
     * Применение экспоненты
     * @param matrix исходная матрица
     * @return       матрица после применения операции
     */
    public static Matrix exp(Matrix matrix) {
        return applyToEachCell(matrix, expOperation);
    }

    /**
     * Применение гиперболического тангенса
     * @param matrix исходная матрица
     * @return       матрица после применения операции
     */
    public static Matrix tanh(Matrix matrix) {
        return applyToEachCell(matrix, tanhOperation);
    }

    /**
     * Получение абсолютных значений
     * @param matrix исходная матрица
     * @return       матрица после применения операции
     */
    public static Matrix abs(Matrix matrix) {
        return applyToEachCell(matrix, absOperation);
    }

    /**
     * Возведение в степень
     * @param matrix исходная матрица
     * @param scale  степень
     * @return       матрица после применения операции
     */
    public static Matrix pow(Matrix matrix, double scale) {
        powOperation = new Pow(scale);
        return applyToEachCell(matrix, powOperation);
    }

    /**
     * Применение операции к каждому элементу матрицы
     * @param matrix    матрица
     * @param operation операция
     * @return          матрица после применения операции
     */
    private static Matrix applyToEachCell(Matrix matrix, DoubleOperation operation) {
        double[][] result = new double[matrix.getRows()][matrix.getCols()];
        for (int row = 0; row < matrix.getRows(); row++)
            for (int col = 0; col < matrix.getCols(); col++)
                result[row][col] = operation.apply(matrix.getValue(row, col));
        return new Matrix(result);
    }
}
