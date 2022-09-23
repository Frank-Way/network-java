package models.data;

import models.math.Matrix;
import models.math.MatrixUtils;
import utils.copy.DeepCopyable;

import java.util.Iterator;
import java.util.Objects;

/**
 * Элемент выборки, объединяющий входные и соответствующие им выходные значения. Атрибуты модели:
 * <pre><ul>
 *  <li>inputs  - входные значения;</li>
 *  <li>outputs - выходные значения.</li>
 * </ul></pre>
 */
public class Data implements DeepCopyable {
    private final Matrix inputs;
    private final Matrix outputs;

    /**
     * Конструктор, см. описание параметров в {@link Data}.
     * Проверяется совпадение количества строк для входов и выходов, при несовпадении выбрасывается
     * IllegalArgumentException
     */
    public Data(Matrix inputs, Matrix outputs) {
        if (inputs.getRows() != outputs.getRows())
            throw new IllegalArgumentException(String.format(
                    "Количество строк выборки не совпадает для входов (%d; %d) и выходов (%d; %d)",
                    inputs.getRows(), inputs.getCols(), outputs.getRows(), outputs.getCols()));
        this.inputs = inputs;
        this.outputs = outputs;
    }

    /**
     * Получение объекта для разбиения выборки на пакеты заданного размера для последующей итерации по нему
     * @param batchSize    размер выборки
     * @param needsShuffle требуется ли перемешивание выборки
     * @param needsNoise   требуется ли зашумление выборки
     * @return             итерируемый объект
     */
    public Iterable<Data> getBatchesGenerator(int batchSize, boolean needsShuffle, boolean needsNoise) {
        return () -> new Iterator<Data>() {
            private int currentIndex = 0;  // текущий индекс, откуда начнется новый пакет
            // индексы для перемешивания выборки
            private final int[] indices = MatrixUtils.getRandomRangePermutation(getRows());
            // входы и выходы выборки (полностью)
            private final Matrix x = needsShuffle ? inputs.shuffle(indices, 0) : inputs;
            private final Matrix y = needsShuffle ? outputs.shuffle(indices, 0) : outputs;
            private final Matrix noise = needsNoise ?
                    MatrixUtils.getRandomMatrixNormal(inputs.getRows(), inputs.getCols(),
                            0.0, 1.0 / inputs.size()) :
                    inputs.zerosLike();

            /**
             * Проверка наличия следующего пакета
             * @return  true, если есть ещё пакеты
             */
            @Override
            public boolean hasNext() {
                return currentIndex < getRows();
            }

            /**
             * Получение следующего пакета
             * @return пакет
             */
            @Override
            public Data next() {
                // индекс конца пакета (с проверкой на соответствие диапазону)
                int newIndex = Math.min(currentIndex + batchSize, getRows());
                // новый пакет формируется из срезов от currentIndex до newIndex
                Data result = new Data(x.getRowSlice(currentIndex, newIndex, 1)
                                         .add(noise.getRowSlice(currentIndex, newIndex, 1)),
                        y.getRowSlice(currentIndex, newIndex, 1));
                currentIndex = newIndex;  // сохранение последнего индекса
                return result;  // возвращение пакета
            }
        };
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

    @Override
    public String toString() {
        return "Data{" +
                "inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }

    @Override
    public Data deepCopy() {
        return new Data(inputs.deepCopy(), outputs.deepCopy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return inputs.equals(data.inputs) && outputs.equals(data.outputs);
    }

    @Override
    public int hashCode() {
        return Objects.hash(inputs, outputs);
    }
}
