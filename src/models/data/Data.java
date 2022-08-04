package models.data;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.math.Matrix;
import models.math.MatrixOperations;
import utils.Utils;

import java.util.Iterator;

/**
 * Элемент выборки, объединяющий входные и соответствующие им выходные значения. Атрибуты модели:
 *  inputs - входные значения;
 *  outputs - выходные значения.
 */
public class Data implements Copyable<Data> {
    private final Matrix inputs;
    private final Matrix outputs;

    /**
     * Конструктор. Проверяется совпадение количества строк для входов и выходов, при несовпадении
     * выбрасывается исключение
     * @param inputs входные значения
     * @param outputs выходные значения
     */
    public Data(@NotNull Matrix inputs, @NotNull Matrix outputs) {
        if (inputs.getRows() != outputs.getRows())
            throw new IllegalArgumentException(String.format(
                    "Количество строк выборки не совпадает для входов (%d; %d) и выходов (%d; %d)",
                    inputs.getRows(), inputs.getCols(), outputs.getRows(), outputs.getCols()));
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public Matrix getInputs() {
        return inputs;
    }

    public Matrix getOutputs() {
        return outputs;
    }

    public int getRows() {
        return inputs.getRows();
    }

    /**
     * Получение объекта для разбиения выборки на пакеты заданного размера для последующей итерации по нему
     * @param batchSize размер выборки
     * @param needsShuffle требуется ли перемешивание выборки
     * @return итерируемый объект
     */
    public Iterable<Data> getBatchesGenerator(int batchSize, boolean needsShuffle) {
        return () -> new Iterator<Data>() {
            private int currentIndex = 0;
            private final int[] indices = MatrixOperations.getRandomRangePermutation(getRows());
            private final Matrix x = needsShuffle ? inputs.shuffle(indices, 0) : inputs;
            private final Matrix y = needsShuffle ? outputs.shuffle(indices, 0) : outputs;

            @Override
            public boolean hasNext() {
                return currentIndex < getRows();
            }

            @Override
            public Data next() {
                int newIndex = Math.min(currentIndex + batchSize, getRows());
                Data result = new Data(x.getRowSlice(currentIndex, newIndex, 1),
                        y.getRowSlice(currentIndex, newIndex, 1));
                currentIndex = newIndex;
                return result;
            }
        };
    }

    @Override
    public Data copy() {
        return new Data(Utils.copyNullable(inputs), Utils.copyNullable(outputs));
    }

    @Override
    public String toString() {
        return "Data{" +
                "inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }
}
