package utils;

import com.sun.istack.internal.NotNull;
import models.data.Dataset;
import models.interfaces.Debuggable;
import models.math.functions.Functions;
import models.math.Matrix;
import models.networks.Network;
import models.trainers.FitResults;
import models.trainers.Trainer;

import java.util.Objects;

public class TrainResults implements Debuggable {
    private final double maxAbsoluteError;
    private final double maxRelativeError;
    private final double meanAbsoluteError;
    private final Network network;
    private final Dataset dataset;
    private final FitResults fitResults;

    public TrainResults(@NotNull Network network, @NotNull Dataset dataset, @NotNull FitResults fitResults) {
        this.network = fitResults.getBestNetwork();
        this.dataset = dataset;
        this.fitResults = fitResults;

        Matrix validInputs = dataset.getValidData().getInputs();
        Matrix validOutputs = dataset.getValidData().getOutputs();
        Matrix predictions = this.network.forward(validInputs);
        Matrix errors = predictions.sub(validOutputs);
        Matrix absoluteErrors = Functions.abs(errors);

        maxAbsoluteError = absoluteErrors.max();
        double rangeLength = validOutputs.max() - validOutputs.min();
        maxRelativeError = maxAbsoluteError / rangeLength * 100;
        meanAbsoluteError = absoluteErrors.sum() / absoluteErrors.size();
    }

    public double getMaxAbsoluteError() {
        return maxAbsoluteError;
    }

    public double getMaxRelativeError() {
        return maxRelativeError;
    }

    public double getMeanAbsoluteError() {
        return meanAbsoluteError;
    }

    public FitResults getFitResults() {
        return fitResults;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TrainResults)) return false;
        TrainResults that = (TrainResults) o;
        return Double.compare(that.getMaxAbsoluteError(), getMaxAbsoluteError()) == 0 &&
               Double.compare(that.getMaxRelativeError(), getMaxRelativeError()) == 0 &&
               Double.compare(that.getMeanAbsoluteError(), getMeanAbsoluteError()) == 0 &&
               Objects.equals(network, that.network) &&
                Objects.equals(dataset, that.dataset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxAbsoluteError, maxRelativeError, meanAbsoluteError, network, dataset);
    }

    @Override
    public String toString() {
        return "TrainResults{" +
                "maxAbsoluteError=" + maxAbsoluteError +
                ", maxRelativeError=" + maxRelativeError +
                ", meanAbsoluteError=" + meanAbsoluteError +
                ", network=" + network +
                ", dataset=" + dataset +
                ", fitResults=" + fitResults +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "РезультатыОбучения{" +
                "максимальнаяАбсолютнаяОшибка=" + maxAbsoluteError +
                ", максимальнаяОтносительнаяОшибка=" + maxRelativeError +
                ", средняяАбсолютнаяОшибка=" + meanAbsoluteError +
                '}';
    }
}
