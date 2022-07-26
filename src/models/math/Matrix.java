package models.math;

import models.interfaces.Copyable;

import java.io.Serializable;
import java.util.*;

public class Matrix implements Copyable<Matrix>, Serializable {
    private final double[][] values;
    private final int rows;
    private final int cols;

    public Matrix(double[][] values) {
        this.values = values;
        this.rows = values.length;
        this.cols = values[0].length;
    }

    @Override
    public Matrix copy() {
        double[][] result = new double[getRows()][getCols()];
        for (int row = 0; row < getRows(); row++)
            result[row] = Arrays.copyOf(this.values[row], this.getCols());
        return new Matrix(result);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return getRows() == matrix.getRows() && getCols() == matrix.getCols() && Arrays.deepEquals(getValues(), matrix.getValues());
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(getRows(), getCols());
        result = 31 * result + Arrays.deepHashCode(getValues());
        return result;
    }

    @Override
    public String toString() {
        return "Matrix {" +
                "rows=" + rows +
                ", cols=" + cols +
                '}';
    }

    public String valuesToString(String format) {
        StringBuilder result = new StringBuilder();
        result.append('[');
        for (int row = 0; row < getRows(); row++) {
            if (row > 0)
                result.append(' ');
            result.append('[');
            for (int col = 0; col < getCols(); col++)
                result.append(String.format(format, getValues()[row][col]));
            result.append(']');
            if (row < getRows() - 1)
                result.append('\n');
        }
        result.append(']');
        return result.toString();
    }

    public String valuesToString() {
        return valuesToString("%10.5f");
    }

    public double[][] getValues() {
        return this.values;
    }

    public int size() {
        return rows * cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public Matrix getRow(int row) {
        double[][] result = new double[1][getCols()];
        for (int col = 0; col < getCols(); col++)
            result[0][col] = getValues()[row][col];
        return new Matrix(result);
    }

    public Matrix getCol(int col) {
        double[][] result = new double[getRows()][1];
        for (int row = 0; row < getRows(); row++)
            result[row][0] = getValues()[row][col];
        return new Matrix(result);
    }

    public Matrix mulMatrix(Matrix matrix) {
        checkColsRows(matrix,
                "Матрица размерности (%d; %d) не может быть умножена на предоставленную матрицу размера (%d; %d)");
        double[][] result = new double[getRows()][matrix.getCols()];
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < matrix.getCols(); col++)
                result[row][col] = MatrixOperations.mulScalar(getRow(row).transpose(), matrix.getCol(col));
        return new Matrix(result);
    }

    public Matrix mul(Matrix matrix) {
        checkRowsAndCols(matrix,
                "Матрица размерности (%d; %d) не может быть поэлементно умножена на предоставленную матрицу размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] *= matrix.getValues()[row][col];
        return new Matrix(result);
    }

    public Matrix mulCol(Matrix colMatrix) {
        checkRows(colMatrix,
                "Матрица размерности (%d; %d) не может быть умножена на предоставленный вектор-столбец размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] *= colMatrix.getValues()[row][0];
        return new Matrix(result);
    }

    public Matrix mulRow(Matrix rowMatrix) {
        checkCols(rowMatrix,
                "Матрица размерности (%d; %d) не может быть умножена на предоставленный вектор-строка размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] *= rowMatrix.getValues()[0][col];
        return new Matrix(result);
    }

    public Matrix mul(Number number) {
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] *= number.doubleValue();
        return new Matrix(result);
    }

    public Matrix add(Matrix matrix) {
        checkRowsAndCols(matrix,
                "Матрица размерности (%d; %d) не может быть сложена с предоставленной матрицей размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] += matrix.getValues()[row][col];
        return new Matrix(result);
    }

    public Matrix addCol(Matrix colMatrix) {
        checkRows(colMatrix,
                "Матрица размерности (%d; %d) не может быть сложена с предоставленным вектором-столбцом размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] += colMatrix.getValues()[row][0];
        return new Matrix(result);
    }

    public Matrix addRow(Matrix rowMatrix) {
        checkCols(rowMatrix,
                "Матрица размерности (%d; %d) не может быть сложена с предоставленным вектором-строкой размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++) {
                result[row][col] += rowMatrix.getValues()[0][col];
            }
        return new Matrix(result);
    }

    public Matrix add(Number number) {
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] += number.doubleValue();
        return new Matrix(result);
    }

    public Matrix sub(Matrix matrix) {
        checkRowsAndCols(matrix,
                "Из матрицы размерности (%d; %d) не может быть вычтена предоставленная матрица размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] -= matrix.getValues()[row][col];
        return new Matrix(result);
    }

    public Matrix subCol(Matrix colMatrix) {
        checkRows(colMatrix,
                "Из матрицы размерности (%d; %d) не может быть вычтен предоставленный вектор-столбец размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] -= colMatrix.getValues()[row][0];
        return new Matrix(result);
    }

    public Matrix subRow(Matrix rowMatrix) {
        checkCols(rowMatrix,
                "Из матрицы размерности (%d; %d) не может быть вычтен предоставленный вектор-строка размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] -= rowMatrix.getValues()[0][col];
        return new Matrix(result);
    }

    public Matrix sub(Number number) {
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] -= number.doubleValue();
        return new Matrix(result);
    }

    public Matrix div(Matrix matrix) {
        checkRowsAndCols(matrix,
                "Матрица размерности (%d; %d) не может быть поделена на предоставленную матрицу размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] /= matrix.getValues()[row][col];
        return new Matrix(result);
    }

    public Matrix divCol(Matrix colMatrix) {
        checkRows(colMatrix,
                "Матрица размерности (%d; %d) не может быть поделена на предоставленный вектор-столбец размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] /= colMatrix.getValues()[row][0];
        return new Matrix(result);
    }

    public Matrix divRow(Matrix rowMatrix) {
        checkCols(rowMatrix,
                "Матрица размерности (%d; %d) не может быть поделена на предоставленный вектор-строку размера (%d; %d)");
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] /= rowMatrix.getValues()[0][col];
        return new Matrix(result);
    }

    public Matrix div(Number number) {
        double[][] result = copy().getValues();
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] /= number.doubleValue();
        return new Matrix(result);
    }

    public double sum() {
        double result = 0.0;
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result += getValues()[row][col];
        return result;
    }

    public Matrix sum(int axis) {
        double[][] result;
        double s;
        switch (axis) {
            case 0:
                result = new double[getRows()][1];
                for (int row = 0; row < getRows(); row++) {
                    s = 0;
                    for (int col = 0; col < getCols(); col++)
                        s += getValues()[row][col];
                    result[row][0] = s;
                }
                return new Matrix(result);
            case 1:
                result = new double[1][getCols()];
                for (int col = 0; col < getCols(); col++) {
                    s = 0;
                    for (int row = 0; row < getRows(); row++)
                        s += getValues()[row][col];
                    result[0][col] = s;
                }
                return new Matrix(result);
            default:
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    public double min() {
        double result = Double.MAX_VALUE;
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result = Math.min(result, getValues()[row][col]);
        return result;
    }

    public Matrix min(int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[getRows()][1];
                for (int row = 0; row < getRows(); row++) {
                    for (int col = 0; col < getCols(); col++)
                        result[row][0] = Math.min(result[row][0], getValues()[row][col]);
                }
                return new Matrix(result);
            case 1:
                result = new double[1][getCols()];
                for (int col = 0; col < getCols(); col++) {
                    for (int row = 0; row < getRows(); row++)
                        result[0][col] = Math.min(result[0][col], getValues()[row][col]);
                }
                return new Matrix(result);
            default:
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    public double max() {
        double result = Double.MIN_VALUE;
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result = Math.max(result, getValues()[row][col]);
        return result;
    }

    public Matrix max(int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[getRows()][1];
                for (int row = 0; row < getRows(); row++) {
                    for (int col = 0; col < getCols(); col++)
                        result[row][0] = Math.max(result[row][0], getValues()[row][col]);
                }
                return new Matrix(result);
            case 1:
                result = new double[1][getCols()];
                for (int col = 0; col < getCols(); col++) {
                    for (int row = 0; row < getRows(); row++)
                        result[0][col] = Math.max(result[0][col], getValues()[row][col]);
                }
                return new Matrix(result);
            default:
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    public Matrix transpose() {
        double[][] result = new double[getCols()][getRows()];
        for (int row = 0; row < getRows(); row++)
            for (int col = 0; col < getCols(); col++)
                result[col][row] = getValues()[row][col];
        return new Matrix(result);
    }

    public Matrix getSlice(int start, int stop, int step) {
        if (start > stop || start > getRows() || stop > getRows())
            throw new IllegalArgumentException(String.format(
                    "Недопустимые аргументы для среза (start=%d, stop=%d, step=%d)", start, stop, step));
        int rows = (int)Math.ceil((stop - start) * 1.0 / step);
        double[][] result = new double[rows][getCols()];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < getCols(); col++)
                result[row][col] = getValues()[start + row][col];
        return new Matrix(result);
    }

    public Matrix onesLike() {
        return new Matrix(new double[getRows()][getCols()]).add(1);
    }

    public Matrix zerosLike() {
        return new Matrix(new double[getRows()][getCols()]);
    }

    public List<Matrix> getBatches(int batchSize) {
        List<Matrix> result = new ArrayList<>();
        for (int i = 0; i < getRows(); i = i + batchSize) {
            result.add(getSlice(i, Math.min(i + batchSize, getRows()), 1));
        }
        return result;
    }

    public Matrix extend(int factor, int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[getRows() * factor][getCols()];
                for (int row = 0; row < getRows(); row++)
                    for (int col = 0; col < getCols(); col++)
                        for (int i = 0; i < factor; i++)
                            result[row * factor + i][col] = getValues()[row][col];
                return new Matrix(result);
            case 1:
                result = new double[getRows()][getCols() * factor];
                for (int row = 0; row < getRows(); row++)
                    for (int col = 0; col < getCols(); col++)
                        for (int i = 0; i < factor; i++)
                            result[row][col * factor + i] = getValues()[row][col];
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
                result = new double[getRows()][getCols() + matrix.getCols()];
                for (int row = 0; row < getRows(); row++) {
                    for (int col1 = 0; col1 < getCols(); col1++) {
                        result[row][col1] = getValues()[row][col1];
                    }
                    for (int col2 = 0; col2 < matrix.getCols(); col2++)
                        result[row][getCols() + col2] = matrix.getValues()[row][col2];
                }
                return new Matrix(result);
            case 1:
                checkCols(matrix,
                        "Матрица размерностью (%d; %d) не может быть вертикально конкатенирована" +
                                " с матрицей размерности (%d; %d)");
                result = new double[getRows() + matrix.getRows()][getCols()];
                for (int col = 0; col < getCols(); col++) {
                    for (int row1 = 0; row1 < getRows(); row1++)
                        result[row1][col] = getValues()[row1][col];
                    for (int row2 = 0; row2 < matrix.getRows(); row2++)
                        result[getRows() + row2][col] = matrix.getValues()[row2][col];
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

    public Matrix exp() {
        return MatrixOperations.Functions.exp(this);
    }

    public Matrix pow(double scale) {
        return MatrixOperations.Functions.pow(this, scale);
    }

    protected boolean isRowsAndColsEqual(Matrix matrix) {
        return isRowsEqual(matrix) && isColsEqual(matrix);
    }

    protected boolean isRowsEqual(Matrix matrix) {
        return this.getRows() == matrix.getRows();
    }

    protected boolean isColsEqual(Matrix matrix) {
        return this.getCols() == matrix.getCols();
    }

    protected boolean isRowsColsEqual(Matrix matrix) {
        return this.getRows() == matrix.getCols();
    }

    protected boolean isColsRowsEqual(Matrix matrix) {
        return this.getCols() == matrix.getRows();
    }

    private void checkRows(Matrix matrix, String message) {
        if (!isRowsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.getRows(), this.getCols(), matrix.getRows(), matrix.getCols()));
    }

    private void checkCols(Matrix matrix, String message) {
        if (!isColsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.getRows(), this.getCols(), matrix.getRows(), matrix.getCols()));
    }

    private void checkRowsCols(Matrix matrix, String message) {
        if (!isRowsColsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.getRows(), this.getCols(), matrix.getRows(), matrix.getCols()));
    }

    private void checkColsRows(Matrix matrix, String message) {
        if (!isColsRowsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.getRows(), this.getCols(), matrix.getRows(), matrix.getCols()));
    }

    private void checkRowsAndCols(Matrix matrix, String message) {
        checkRows(matrix, message);
        checkCols(matrix, message);
    }

}