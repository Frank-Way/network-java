package utils;

import models.data.Dataset;
import models.math.functions.Functions;
import models.math.Matrix;
import models.networks.Network;
import models.trainers.FitResults;

import java.util.Objects;

public class TrainResults implements Debuggable {
    protected double maxAbsoluteError;
    protected double maxRelativeError;
    protected double meanAbsoluteError;
    protected Network network;
    protected Dataset dataset;
    protected FitResults fitResults;

    public TrainResults(Network network, Dataset dataset, FitResults fitResults) {
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
        if (o == null || getClass() != o.getClass()) return false;
        TrainResults that = (TrainResults) o;
        return Double.compare(that.getMaxAbsoluteError(), getMaxAbsoluteError()) == 0 && Double.compare(that.getMaxRelativeError(), getMaxRelativeError()) == 0 && Double.compare(that.getMeanAbsoluteError(), getMeanAbsoluteError()) == 0 && network.equals(that.network) && dataset.equals(that.dataset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMaxAbsoluteError(), getMaxRelativeError(), getMeanAbsoluteError(), network, dataset);
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
