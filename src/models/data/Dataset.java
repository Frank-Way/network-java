package models.data;

import utils.copy.DeepCopyable;

import java.util.Comparator;
import java.util.stream.Stream;

/**
 * Обучающая выборка, состоящая из трёх частей типа {@link Data}:
 * <pre><ul>
 *  <li>trainData - данные для обучения (предполагается использование при обучении);</li>
 *  <li>testData  - данные для тестов (предполагается использование для оценки во время обучения);</li>
 *  <li>validData - данные для валидации (предполагается использование для оценки после обучения).</li>
 * </ul></pre>
 */
public class Dataset implements DeepCopyable {
    private final Data validData;
    private final Data testData;
    private final Data trainData;

    /**
     * Конструктор, см описание параметров в {@link Dataset}
     */
    public Dataset(Data trainData, Data testData, Data validData) {
        this.validData = validData;
        this.testData = testData;
        this.trainData = trainData;
    }

    public Data getValidData() {
        return validData;
    }

    public Data getTestData() {
        return testData;
    }

    public Data getTrainData() {
        return trainData;
    }

    public double getMinValue() {
        return Stream.of(trainData, testData, validData)
                .map(data -> Math.min(data.getInputs().min(), data.getOutputs().min()))
                .min(Comparator.comparingDouble(Double::doubleValue))
                .orElse(Double.MAX_VALUE);
    }

    public double getMaxValue() {
        return Stream.of(trainData, testData, validData)
                .map(data -> Math.max(data.getInputs().max(), data.getOutputs().max()))
                .min(Comparator.comparingDouble(Double::doubleValue))
                .orElse(Double.MIN_VALUE);
    }

    public int getInputsCount() {
        return trainData.getInputs().getCols();
    }

    public int getOutputsCount() {
        return trainData.getOutputs().getCols();
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "validData=" + validData +
                ", testData=" + testData +
                ", trainData=" + trainData +
                '}';
    }

    @Override
    public Dataset deepCopy() {
        return new Dataset(trainData.deepCopy(), testData.deepCopy(), validData.deepCopy());
    }
}
