package models.optimizers;

import models.networks.Network;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Билдер для {@link Optimizer}
 */
@YamlSerializable
public abstract class OptimizerBuilder implements DeepCopyable, Serializable {
    protected transient Network network;
    protected transient double learningRate;
    protected transient double decayLR;
    @YamlField protected double startLR;
    @YamlField protected double stopLR;
    protected transient int epochs;

    public OptimizerBuilder() {
    }

    protected OptimizerBuilder(Network network, double learningRate, double decayLR, double startLR, double stopLR, int epochs) {
        this.network = network;
        this.learningRate = learningRate;
        this.decayLR = decayLR;
        this.startLR = startLR;
        this.stopLR = stopLR;
        this.epochs = epochs;
    }

    public OptimizerBuilder network(Network network) {
        this.network = network;
        return this;
    }

    public OptimizerBuilder startLR(double startLR) {
        this.startLR = startLR;
        return this;
    }

    public OptimizerBuilder stopLR(double stopLR) {
        this.stopLR = stopLR;
        return this;
    }

    public OptimizerBuilder epochs(int epochs) {
        this.epochs = epochs;
        return this;
    }

    public abstract Optimizer build();

    @Override
    public abstract OptimizerBuilder deepCopy();

    protected void validate() {
        if (network == null)
            throw new IllegalStateException("Не задана сеть для построения оптимизатора");
        if (startLR < stopLR)
            throw new IllegalStateException("Неверно заданы начальная и конечная скорости обучения");
    }

    protected void prepare() {
        decayLR = (startLR - stopLR) / (epochs - 1);
        learningRate = startLR;
    }

    public double getStartLR() {
        return startLR;
    }

    public double getStopLR() {
        return stopLR;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptimizerBuilder that = (OptimizerBuilder) o;
        return Double.compare(that.learningRate, learningRate) == 0 &&
                Double.compare(that.decayLR, decayLR) == 0 &&
                Double.compare(that.startLR, startLR) == 0 &&
                Double.compare(that.stopLR, stopLR) == 0 &&
                epochs == that.epochs &&
                Objects.equals(network, that.network);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, learningRate, decayLR, startLR, stopLR, epochs);
    }

    @Override
    public String toString() {
        return "OptimizerBuilder{" +
                "network=" + network +
                ", learningRate=" + learningRate +
                ", decayLR=" + decayLR +
                ", startLR=" + startLR +
                ", stopLR=" + stopLR +
                ", epochs=" + epochs +
                '}';
    }
}
