package models.optimizers;

import models.layers.Layer;
import models.math.Matrix;
import models.networks.Network;
import models.operations.Operation;
import models.operations.ParametrizedOperation;
import utils.Debuggable;

import java.util.Objects;

public abstract class Optimizer implements Cloneable, Debuggable {
    protected Network network;
    protected double learningRate;
    protected double decayLR;
    protected double startLR;
    protected double stopLR;
    protected int epochs;

    public Optimizer(double startLR, double stopLR) {
        learningRate = startLR;
        this.startLR = startLR;
        this.stopLR = stopLR;
    }

    public void setNetwork(Network network) {
        this.network = network;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    public void step() {
        for (Layer layer: network.getLayers()) {
            for (int i = 0; i < layer.getOperations().size(); i++) {
                Operation operation = layer.getOperation(i);
                if (operation instanceof ParametrizedOperation) {
                    ParametrizedOperation parametrizedOperation = (ParametrizedOperation)operation;
                    parametrizedOperation.setParameter(update(parametrizedOperation.getParameter(), parametrizedOperation.getParameterGradient()));
                }
            }
        }
    }

    public void calculateDecayLR() {
        decayLR = (startLR - stopLR) / (epochs - 1);
    }

    public void decay() {
        learningRate -= decayLR;
    }

    protected abstract Matrix update(Matrix parameters, Matrix parameterGradients);

    @Override
    public Optimizer clone() {
        try {
            Optimizer clone = (Optimizer) super.clone();
            clone.network = network.clone();
            clone.learningRate = learningRate;
            clone.decayLR = decayLR;
            clone.startLR = startLR;
            clone.stopLR = stopLR;
            clone.epochs = epochs;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public Network getNetwork() {
        return network;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Optimizer optimizer = (Optimizer) o;
        return Double.compare(optimizer.learningRate, learningRate) == 0 && Double.compare(optimizer.decayLR, decayLR) == 0 && Double.compare(optimizer.startLR, startLR) == 0 && Double.compare(optimizer.stopLR, stopLR) == 0 && epochs == optimizer.epochs && getNetwork().equals(optimizer.getNetwork());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNetwork(), learningRate, decayLR, startLR, stopLR, epochs);
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "network=" + network +
                ", learningRate=" + learningRate +
                ", decayLR=" + decayLR +
                ", startLR=" + startLR +
                ", stopLR=" + stopLR +
                ", epochs=" + epochs +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                "нейросеть=" + network.toString(debugMode) +
                '}';
    }

    protected abstract String getClassName();

    protected abstract String getDebugClassName();
}
