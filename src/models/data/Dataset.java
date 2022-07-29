package models.data;

import models.interfaces.Copyable;
import utils.Utils;

import java.util.Objects;

public class Dataset implements Copyable<Dataset> {
    private final Data validData;
    private final Data testData;
    private final Data trainData;

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dataset dataset = (Dataset) o;
        return Objects.equals(getValidData(), dataset.getValidData()) &&
                Objects.equals(getTestData(), dataset.getTestData()) &&
                Objects.equals(getTrainData(), dataset.getTrainData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getValidData(), getTestData(), getTrainData());
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
