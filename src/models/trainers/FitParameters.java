package models.trainers;

import models.data.Dataset;
import utils.Debuggable;

import java.util.Objects;

public class FitParameters implements Cloneable, Debuggable {
    protected Dataset dataset;
    protected int epochs;
    protected int batchSize;
    protected int queries;
    protected boolean earlyStopping;
    protected String doubleFormat;

    public FitParameters(Dataset dataset, int epochs, int batchSize, int queries, boolean earlyStopping, String doubleFormat) {
        this.dataset = dataset;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.queries = queries;
        this.earlyStopping = earlyStopping;
        this.doubleFormat = doubleFormat;
    }

    public FitParameters(Dataset dataset, int epochs, int batchSize, int queries, boolean earlyStopping) {
        this.dataset = dataset;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.queries = queries;
        this.earlyStopping = earlyStopping;
        this.doubleFormat = "%10.5f";
    }

    public Dataset getDataset() {
        return dataset;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public int getEpochs() {
        return epochs;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public int getQueries() {
        return queries;
    }

    public void setQueries(int queries) {
        this.queries = queries;
    }

    public boolean isEarlyStopping() {
        return earlyStopping;
    }

    public void setEarlyStopping(boolean earlyStopping) {
        this.earlyStopping = earlyStopping;
    }

    @Override
    public FitParameters clone() {
        try {
            FitParameters clone = (FitParameters) super.clone();
            clone.dataset = dataset.clone();
            clone.epochs = epochs;
            clone.batchSize = batchSize;
            clone.queries = queries;
            clone.earlyStopping = earlyStopping;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FitParameters that = (FitParameters) o;
        return getEpochs() == that.getEpochs() && getBatchSize() == that.getBatchSize() && getQueries() == that.getQueries() && isEarlyStopping() == that.isEarlyStopping() && getDataset().equals(that.getDataset());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getDataset(), getEpochs(), getBatchSize(), getQueries(), isEarlyStopping());
    }

    @Override
    public String toString() {
        return "FitParameters{" +
                "dataset=" + dataset +
                ", epochs=" + epochs +
                ", batchSize=" + batchSize +
                ", queries=" + queries +
                ", earlyStopping=" + earlyStopping +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "ПараметрыОбучения{" +
                "размерВыборкиОбучения=" + dataset.getTrainData().getRows() +
                ", эпох=" + epochs +
                '}';
    }
}
