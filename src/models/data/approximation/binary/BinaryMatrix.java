package models.data.approximation.binary;

import models.math.Matrix;
import utils.copy.DeepCopyable;

import java.io.Serializable;

public class BinaryMatrix implements DeepCopyable, Serializable {
    private final BitArray[][] values;
    private final int rows;
    private final int cols;
    private final int bits;


    /**
     * Конструктор
     * @param values значения
     */
    public BinaryMatrix(BitArray[][] values) {
        this.values = values;
        this.rows = values.length;
        this.cols = values[0].length;
        this.bits = values[0][0].length();
    }

    public BinaryMatrix(Matrix matrix, int reducingFactor) {
        this(getValuesFromMatrix(matrix, reducingFactor));
    }

    /**
     * Конструктор для сериализации
     */
    private BinaryMatrix() {
        this(new BitArray[1][1]);
    }

    public Matrix toMatrix() {
        final BinaryCoder binaryCoder = new BinaryCoder();
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = binaryCoder.decodeDouble(values[row][col]);
        return new Matrix(result);
    }

    private static BitArray[][] getValuesFromMatrix(Matrix matrix, int reducingFactor) {
        final BinaryCoder binaryCoder = new BinaryCoder();
        final int rows = matrix.getRows();
        final int cols = matrix.getCols();

        final BitArray[][] result = new BitArray[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = binaryCoder.code(matrix.getValue(row, col), reducingFactor);
        return result;
    }

    @Override
    public BinaryMatrix deepCopy() {
        final BitArray[][] newValues = new BitArray[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                newValues[row][col] = values[row][col].get(0, bits);
        return new BinaryMatrix(newValues);
    }
}
