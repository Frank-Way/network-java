package models.data;

import models.interfaces.Copyable;
import utils.Utils;

/**
 * Обучающая выборка, состоящая из трёх частей типа {@link Data}:
 *  trainData - данные для обучения (предполагается использование при обучении);
 *  testData - данные для тестов (предполагается использование для оценки во время обучения);
 *  validData - данные для валидации (предполагается использование для оценки после обучения).
 */
public class Dataset implements Copyable<Dataset> {
    private final Data validData;
    private final Data testData;
    private final Data trainData;

    /**
     * Конструктор
     * @param trainData данные для обучения
     * @param testData данные для тестов
     * @param validData данные для валидации
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

    @Override
    public Dataset copy() {
        return new Dataset(Utils.copyNullable(trainData), Utils.copyNullable(testData), Utils.copyNullable(validData));
    }

    @Override
    public String toString() {
        return "Dataset{" +
                "validData=" + validData +
                ", testData=" + testData +
                ", trainData=" + trainData +
                '}';
    }
}
