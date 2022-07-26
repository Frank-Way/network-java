package utils;

import com.sun.istack.internal.NotNull;
import models.data.Dataset;
import models.interfaces.Debuggable;
import models.math.MatrixOperations;
import models.math.Matrix;
import models.networks.Network;
import models.trainers.FitResults;

import java.util.Objects;

public class TrainResults implements Debuggable {
    private final Errors errors;
    private final Network network;
    private final Dataset dataset;
    private final FitResults fitResults;

    public TrainResults(@NotNull Dataset dataset, @NotNull FitResults fitResults) {
        this.network = fitResults.getBestNetwork();
        this.dataset = dataset;
        this.fitResults = fitResults;

        errors = Errors.buildFromTargetsAndPredictions(dataset.getValidData().getOutputs(),
                network.forward(dataset.getValidData().getInputs()));
    }

    public double getMaxAbsoluteError() {
        return errors.getMaxAbsoluteError();
    }

    public double getMaxRelativeError() {
        return errors.getMaxRelativeError();
    }

    public double getMeanAbsoluteError() {
        return errors.getMeanAbsoluteError();
    }

    public FitResults getFitResults() {
        return fitResults;
    }

    public Network getNetwork() {
        return network;
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
        return Objects.hash(errors, network, dataset);
    }

    @Override
    public String toString() {
        return "TrainResults{" +
                "errors=" + errors +
                ", network=" + network +
                ", dataset=" + dataset +
                ", fitResults=" + fitResults +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "РезультатыОбучения{" +
                "максимальнаяАбсолютнаяОшибка=" + getMaxAbsoluteError() +
                ", максимальнаяОтносительнаяОшибка=" + getMaxRelativeError() +
                ", средняяАбсолютнаяОшибка=" + getMeanAbsoluteError() +
                '}';
    }
}
