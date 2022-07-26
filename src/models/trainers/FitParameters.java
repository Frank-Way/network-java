package models.trainers;

import com.sun.istack.internal.NotNull;
import models.data.Dataset;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.networks.NetworkBuilder;
import models.optimizers.Optimizer;
import utils.Utils;

import java.util.Objects;

public class FitParameters implements Copyable<FitParameters>, Debuggable {
    private final Dataset dataset;
    private final int epochs;
    private final int batchSize;
    private final int queries;
    private final boolean earlyStopping;
    private final String doubleFormat;
    private final boolean preTrainRequired;
    private final int preTrainsCount;
    private final double preTrainReduceFactor;
    private final NetworkBuilder networkBuilder;
    private final Optimizer optimizer;

    public FitParameters(@NotNull Dataset dataset, int epochs, int batchSize, int queries,
                         boolean earlyStopping, String doubleFormat, boolean preTrainRequired,
                         int preTrainsCount, double preTrainReduceFactor, NetworkBuilder networkBuilder,
                         Optimizer optimizer) {
        this.dataset = dataset;
        this.epochs = epochs;
        this.batchSize = batchSize;
        this.queries = queries;
        this.earlyStopping = earlyStopping;
        this.doubleFormat = doubleFormat;
        this.preTrainRequired = preTrainRequired;
        this.preTrainsCount = preTrainsCount;
        this.preTrainReduceFactor = preTrainReduceFactor;
        this.networkBuilder = networkBuilder;
        this.optimizer = optimizer;
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

    public boolean isPreTrainRequired() {
        return preTrainRequired;
    }

    public int getPreTrainsCount() {
        return preTrainsCount;
    }

    public double getPreTrainReduceFactor() {
        return preTrainReduceFactor;
    }

    public NetworkBuilder getNetworkBuilder() {
        return networkBuilder;
    }

    public Optimizer getOptimizer() {
        return optimizer;
    }

    public FitParameters preTrainCopy() {
        return new FitParameters(Utils.copyNullable(dataset),
                (int) (epochs / preTrainReduceFactor), batchSize, 1, false, doubleFormat,
                preTrainRequired, preTrainsCount, preTrainReduceFactor, Utils.copyNullable(networkBuilder),
                Utils.copyNullable(optimizer));
    }

    @Override
    public FitParameters copy() {
        return new FitParameters(Utils.copyNullable(dataset), epochs, batchSize, queries, earlyStopping,
                doubleFormat, preTrainRequired, preTrainsCount, preTrainReduceFactor, Utils.copyNullable(networkBuilder),
                Utils.copyNullable(optimizer));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FitParameters)) return false;
        FitParameters that = (FitParameters) o;
        return epochs == that.epochs &&
               batchSize == that.batchSize &&
               queries == that.queries &&
               preTrainRequired == that.preTrainRequired &&
               earlyStopping == that.earlyStopping &&
               preTrainsCount == that.preTrainsCount &&
               preTrainReduceFactor == that.preTrainReduceFactor &&
               Objects.equals(dataset, that.dataset) &&
               Objects.equals(networkBuilder, that.networkBuilder) &&
               Objects.equals(optimizer, that.optimizer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataset, epochs, batchSize, queries, earlyStopping,
                preTrainRequired, preTrainsCount, preTrainReduceFactor, networkBuilder, optimizer);
    }

    @Override
    public String toString() {
        return "FitParameters{" +
                "dataset=" + dataset +
                ", epochs=" + epochs +
                ", batchSize=" + batchSize +
                ", queries=" + queries +
                ", earlyStopping=" + earlyStopping +
                ", preTrainRequired=" + preTrainRequired +
                ", preTrainsCount=" + preTrainsCount +
                ", preTrainReduceFactor=" + preTrainReduceFactor +
                ", networkBuilder=" + networkBuilder +
                ", optimizer=" + optimizer +
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
