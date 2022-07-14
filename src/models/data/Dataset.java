package models.data;

import java.util.Objects;

public class Dataset implements Cloneable {
    protected Data validData;
    protected Data testData;
    protected Data trainData;

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
    public Dataset clone() {
        try {
            Dataset clone = (Dataset) super.clone();
            clone.validData = validData.clone();
            clone.testData = testData.clone();
            clone.trainData = trainData.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dataset dataset = (Dataset) o;
        return getValidData().equals(dataset.getValidData()) && getTestData().equals(dataset.getTestData()) && getTrainData().equals(dataset.getTrainData());
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
