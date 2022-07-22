package models.trainers;

import com.sun.istack.internal.NotNull;
import models.data.Dataset;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import utils.Utils;

import java.util.Objects;

public class FitParameters implements Copyable<FitParameters>, Debuggable {
    private final Dataset dataset;
    private final int epochs;
    private final int batchSize;
    private final int queries;
    private final boolean earlyStopping;

    private final String doubleFormat;

    public FitParameters(@NotNull Dataset dataset, int epochs, int batchSize, int queries, boolean earlyStopping, String doubleFormat) {
        this.dataset = dataset;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.queries = queries;
        this.earlyStopping = earlyStopping;
        this.doubleFormat = doubleFormat;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public int getEpochs() {
        return epochs;
    }

    public int getBatchSize() {
        return batchSize;
    }

    public int getQueries() {
        return queries;
    }

    public boolean isEarlyStopping() {
        return earlyStopping;
    }

    public String getDoubleFormat() {
        return doubleFormat;
    }

    @Override
    public FitParameters copy() {
        return new FitParameters(Utils.copyNullable(dataset), epochs, batchSize, queries, earlyStopping, doubleFormat);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FitParameters)) return false;
        FitParameters that = (FitParameters) o;
        return epochs == that.epochs &&
               batchSize == that.batchSize &&
               queries == that.queries &&
               earlyStopping == that.earlyStopping &&
               Objects.equals(dataset, that.dataset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataset, epochs, batchSize, queries, earlyStopping);
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
