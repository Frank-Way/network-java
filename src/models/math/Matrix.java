package models.math;

import models.interfaces.Copyable;

import java.io.Serializable;
import java.util.*;

/**
 * Двумерная матрица вещественных чисел. Внутри используется double[][]. Реализует различные полезные методы для работы
 * с матрицами. Каждая операция создаёт новую матрицу, то есть класс не-изменяемый (immutable). Атрибуты модели
 *  values - двумерный массив, хранящий числа;
 *  rows - количество строк;
 *  cols - количество столбцов.
 */
public class Matrix implements Copyable<Matrix>, Serializable {
    private final double[][] values;
    private final int rows;
    private final int cols;

    /**
     * Конструктор
     * @param values значения
     */
    public Matrix(double[][] values) {
        this.values = values;
        this.rows = values.length;
        this.cols = values[0].length;
    }

    /**
     * Является ли матрица вектором-строкой
     * @return результат проверки (количество строк = 1)
     */
    public boolean isRow() {
        return rows == 1;
    }

    /**
     * Является ли матрица вектором-столбцом
     * @return результат проверки (количество столбцов = 1)
     */
    public boolean isCol() {
        return cols == 1;
    }

    @Override
    public Matrix copy() {
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            result[row] = Arrays.copyOf(values[row], cols);
        return new Matrix(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return rows == matrix.rows && cols == matrix.cols && Arrays.deepEquals(values, matrix.values);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(values);
        return result;
    }

    @Override
    public String toString() {
        return "Matrix {" +
                "rows=" + rows +
                ", cols=" + cols +
                '}';
    }

    /**
     * Формирование строки со значениями
     * @param format формат вывода вещественных чисел
     * @return строка со значениями
     */
    public String valuesToString(String format) {
        StringBuilder result = new StringBuilder();
        result.append('[');
        for (int row = 0; row < rows; row++) {
            if (row > 0)
                result.append(' ');
            result.append('[');
            for (int col = 0; col < cols; col++)
                result.append(String.format(format, values[row][col]));
            result.append(']');
            if (row < rows - 1)
                result.append('\n');
        }
        result.append(']');
        return result.toString();
    }

    /**
     * Формирование строки со значениями (формат вывода вещественных чисел - по умолчанию)
     * @return строка со значениями
     */
    public String valuesToString() {
        return valuesToString("%10.5f");
    }

    protected double[][] getValues() {
        return this.values;
    }

    /**
     * Получение значения по строке и столбцу
     * @param row номер строки
     * @param col номер столбца
     * @return значение
     */
    public double getValue(int row, int col) {
        return values[row][col];
    }

    /**
     * Получение копии строки
     * @param row номер строки
     * @return строка
     */
    public double[] getValue(int row) {
        return Arrays.copyOf(values[row], cols);
    }

    /**
     * Количество элементов матрицы
     * @return размер
     */
    public int size() {
        return rows * cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /**
     * Получение вектора-строки
     * @param row номер строки
     * @return вектор-строка
     */
    public Matrix getRow(int row) {
        double[][] result = new double[1][cols];
        for (int col = 0; col < cols; col++)
            result[0][col] = values[row][col];
        return new Matrix(result);
    }

    /**
     * Получение вектора-столбца
     * @param col номер столбца
     * @return вектор-столбец
     */
    public Matrix getCol(int col) {
        double[][] result = new double[rows][1];
        for (int row = 0; row < rows; row++)
            result[row][0] = values[row][col];
        return new Matrix(result);
    }

    public Matrix mulMatrix(Matrix matrix) {
        checkColsRows(matrix,
                "Матрица размерности (%d; %d) не может быть умножена на предоставленную матрицу размера (%d; %d)");
        double[][] result = new double[rows][matrix.cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < matrix.cols; col++)
                result[row][col] = MatrixOperations.mulScalar(getRow(row).transpose(), matrix.getCol(col));
        return new Matrix(result);
    }

    public Matrix mul(Matrix matrix) {
        checkRowsAndCols(matrix,
                "Матрица размерности (%d; %d) не может быть поэлементно умножена на предоставленную матрицу размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] *= matrix.values[row][col];
        return new Matrix(result);
    }

    public Matrix mulCol(Matrix colMatrix) {
        checkRows(colMatrix,
                "Матрица размерности (%d; %d) не может быть умножена на предоставленный вектор-столбец размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] *= colMatrix.values[row][0];
        return new Matrix(result);
    }

    public Matrix mulRow(Matrix rowMatrix) {
        checkCols(rowMatrix,
                "Матрица размерности (%d; %d) не может быть умножена на предоставленный вектор-строка размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] *= rowMatrix.values[0][col];
        return new Matrix(result);
    }

    public Matrix mul(Number number) {
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] *= number.doubleValue();
        return new Matrix(result);
    }

    public Matrix add(Matrix matrix) {
        checkRowsAndCols(matrix,
                "Матрица размерности (%d; %d) не может быть сложена с предоставленной матрицей размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] += matrix.values[row][col];
        return new Matrix(result);
    }

    public Matrix addCol(Matrix colMatrix) {
        checkRows(colMatrix,
                "Матрица размерности (%d; %d) не может быть сложена с предоставленным вектором-столбцом размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] += colMatrix.values[row][0];
        return new Matrix(result);
    }

    public Matrix addRow(Matrix rowMatrix) {
        checkCols(rowMatrix,
                "Матрица размерности (%d; %d) не может быть сложена с предоставленным вектором-строкой размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++) {
                result[row][col] += rowMatrix.values[0][col];
            }
        return new Matrix(result);
    }

    public Matrix add(Number number) {
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] += number.doubleValue();
        return new Matrix(result);
    }

    public Matrix sub(Matrix matrix) {
        checkRowsAndCols(matrix,
                "Из матрицы размерности (%d; %d) не может быть вычтена предоставленная матрица размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] -= matrix.values[row][col];
        return new Matrix(result);
    }

    public Matrix subCol(Matrix colMatrix) {
        checkRows(colMatrix,
                "Из матрицы размерности (%d; %d) не может быть вычтен предоставленный вектор-столбец размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] -= colMatrix.values[row][0];
        return new Matrix(result);
    }

    public Matrix subRow(Matrix rowMatrix) {
        checkCols(rowMatrix,
                "Из матрицы размерности (%d; %d) не может быть вычтен предоставленный вектор-строка размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] -= rowMatrix.values[0][col];
        return new Matrix(result);
    }

    public Matrix sub(Number number) {
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] -= number.doubleValue();
        return new Matrix(result);
    }

    public Matrix div(Matrix matrix) {
        checkRowsAndCols(matrix,
                "Матрица размерности (%d; %d) не может быть поделена на предоставленную матрицу размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] /= matrix.values[row][col];
        return new Matrix(result);
    }

    public Matrix divCol(Matrix colMatrix) {
        checkRows(colMatrix,
                "Матрица размерности (%d; %d) не может быть поделена на предоставленный вектор-столбец размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] /= colMatrix.values[row][0];
        return new Matrix(result);
    }

    public Matrix divRow(Matrix rowMatrix) {
        checkCols(rowMatrix,
                "Матрица размерности (%d; %d) не может быть поделена на предоставленный вектор-строку размера (%d; %d)");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] /= rowMatrix.values[0][col];
        return new Matrix(result);
    }

    public Matrix div(Number number) {
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] /= number.doubleValue();
        return new Matrix(result);
    }

    public double sum() {
        double result = 0.0;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result += values[row][col];
        return result;
    }

    public Matrix sum(int axis) {
        double[][] result;
        double s;
        switch (axis) {
            case 0:
                result = new double[rows][1];
                for (int row = 0; row < rows; row++) {
                    s = 0;
                    for (int col = 0; col < cols; col++)
                        s += values[row][col];
                    result[row][0] = s;
                }
                return new Matrix(result);
            case 1:
                result = new double[1][cols];
                for (int col = 0; col < cols; col++) {
                    s = 0;
                    for (int row = 0; row < rows; row++)
                        s += values[row][col];
                    result[0][col] = s;
                }
                return new Matrix(result);
            default:
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    public double min() {
        double result = Double.MAX_VALUE;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result = Math.min(result, values[row][col]);
        return result;
    }

    public Matrix min(int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[rows][1];
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++)
                        result[row][0] = Math.min(result[row][0], values[row][col]);
                }
                return new Matrix(result);
            case 1:
                result = new double[1][cols];
                for (int col = 0; col < cols; col++) {
                    for (int row = 0; row < rows; row++)
                        result[0][col] = Math.min(result[0][col], values[row][col]);
                }
                return new Matrix(result);
            default:
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    public double max() {
        double result = Double.MIN_VALUE;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result = Math.max(result, values[row][col]);
        return result;
    }

    public Matrix max(int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[rows][1];
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++)
                        result[row][0] = Math.max(result[row][0], values[row][col]);
                }
                return new Matrix(result);
            case 1:
                result = new double[1][cols];
                for (int col = 0; col < cols; col++) {
                    for (int row = 0; row < rows; row++)
                        result[0][col] = Math.max(result[0][col], values[row][col]);
                }
                return new Matrix(result);
            default:
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    public Matrix transpose() {
        double[][] result = new double[cols][rows];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[col][row] = values[row][col];
        return new Matrix(result);
    }

    public Matrix getSlice(int start, int stop, int step) {
        if (start > stop || start > rows || stop > rows)
            throw new IllegalArgumentException(String.format(
                    "Недопустимые аргументы для среза (start=%d, stop=%d, step=%d)", start, stop, step));
        int rows = (int)Math.ceil((stop - start) * 1.0 / step);
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = values[start + row][col];
        return new Matrix(result);
    }

    public Matrix onesLike() {
        return new Matrix(new double[rows][cols]).add(1);
    }

    public Matrix zerosLike() {
        return new Matrix(new double[rows][cols]);
    }

    public List<Matrix> getBatches(int batchSize) {
        List<Matrix> result = new ArrayList<>();
        for (int i = 0; i < rows; i = i + batchSize) {
            result.add(getSlice(i, Math.min(i + batchSize, rows), 1));
        }
        return result;
    }

    public Matrix extend(int factor, int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[rows * factor][cols];
                for (int row = 0; row < rows; row++)
                    for (int col = 0; col < cols; col++)
                        for (int i = 0; i < factor; i++)
                            result[row * factor + i][col] = values[row][col];
                return new Matrix(result);
            case 1:
                result = new double[rows][cols * factor];
                for (int row = 0; row < rows; row++)
                    for (int col = 0; col < cols; col++)
                        for (int i = 0; i < factor; i++)
                            result[row][col * factor + i] = values[row][col];
                return new Matrix(result);
            default:
                throw new IllegalArgumentException(String.format(
                        "Недопустимый параметр extend axis=%d (допустимы: %d, %d)", axis, 0, 1));
        }
    }

    public Matrix stack(Matrix matrix, int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                checkRows(matrix,
                        "Матрица размерностью (%d; %d) не может быть горизонтально конкатенирована" +
                                " с матрицей размерности (%d; %d)");
                result = new double[rows][cols + matrix.cols];
                for (int row = 0; row < rows; row++) {
                    for (int col1 = 0; col1 < cols; col1++) {
                        result[row][col1] = values[row][col1];
                    }
                    for (int col2 = 0; col2 < matrix.cols; col2++)
                        result[row][cols + col2] = matrix.values[row][col2];
                }
                return new Matrix(result);
            case 1:
                checkCols(matrix,
                        "Матрица размерностью (%d; %d) не может быть вертикально конкатенирована" +
                                " с матрицей размерности (%d; %d)");
                result = new double[rows + matrix.rows][cols];
                for (int col = 0; col < cols; col++) {
                    for (int row1 = 0; row1 < rows; row1++)
                        result[row1][col] = values[row1][col];
                    for (int row2 = 0; row2 < matrix.rows; row2++)
                        result[rows + row2][col] = matrix.values[row2][col];
                }
                return new Matrix(result);
            default:
                throw new IllegalArgumentException(String.format(
                        "Недопустимый параметр stack axis=%d (допустимы: %d, %d)", axis, 0, 1));

        }
    }

    public Matrix abs() {
        return MatrixOperations.Functions.abs(this);
    }

    public Matrix pow(double scale) {
        return MatrixOperations.Functions.pow(this, scale);
    }

    protected boolean isRowsAndColsEqual(Matrix matrix) {
        return isRowsEqual(matrix) && isColsEqual(matrix);
    }

    protected boolean isRowsEqual(Matrix matrix) {
        return this.rows == matrix.rows;
    }

    protected boolean isColsEqual(Matrix matrix) {
        return this.cols == matrix.cols;
    }

    protected boolean isRowsColsEqual(Matrix matrix) {
        return this.rows == matrix.cols;
    }

    protected boolean isColsRowsEqual(Matrix matrix) {
        return this.cols == matrix.rows;
    }

    private void checkRows(Matrix matrix, String message) {
        if (!isRowsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    private void checkCols(Matrix matrix, String message) {
        if (!isColsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    private void checkRowsCols(Matrix matrix, String message) {
        if (!isRowsColsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    private void checkColsRows(Matrix matrix, String message) {
        if (!isColsRowsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    private void checkRowsAndCols(Matrix matrix, String message) {
        checkRows(matrix, message);
        checkCols(matrix, message);
    }

}