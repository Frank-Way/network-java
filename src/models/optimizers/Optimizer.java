package models.optimizers;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.layers.Layer;
import models.math.Matrix;
import models.networks.Network;
import models.operations.Operation;
import models.operations.ParametrizedOperation;
import models.interfaces.Debuggable;

import java.util.Objects;

/**
 * Оптимизатор, обучающий сеть по определённому правилу. Атрибуты модели:
 *  {@link Network} - сеть для обучения;
 *  learningRate - скорость обучения;
 *  decayLR - величина снижения скорости обучения;
 *  startLR - начальная скорость обучения;
 *  stopLR - конечная скорость обучения;
 *  epochs - длительность обучения;
 */
public abstract class Optimizer implements Copyable<Optimizer>, Debuggable {
    protected Network network;
    protected double learningRate;
    protected double decayLR;
    protected final double startLR;
    protected final double stopLR;
    protected int epochs;

    /**
     * Конструктор
     * @param startLR  начальная скорость обучения
     * @param stopLR  конечная скорость обучения
     */
    public Optimizer(double startLR, double stopLR) {
        learningRate = startLR;
        this.startLR = startLR;
        this.stopLR = stopLR;
    }

    /***
     * copy-constructor
     */
    protected Optimizer(Network network, double learningRate, double decayLR, double startLR, double stopLR, int epochs) {
        this.network = network;
        this.learningRate = learningRate;
        this.decayLR = decayLR;
        this.startLR = startLR;
        this.stopLR = stopLR;
        this.epochs = epochs;
    }

    public Network getNetwork() {
        return network;
    }

    public void setNetwork(@NotNull Network network) {
        this.network = network;
    }

    public void setEpochs(int epochs) {
        this.epochs = epochs;
    }

    /**
     * Шаг корректировки параметров, выполняемый после обратного прохода, когда все градиенты вычислены
     */
    public void step() {
        for (int l = 0; l < network.layersCount(); l++) {  // перебираем слои
            Layer layer = network.getLayer(l);
            // перебираем операции с параметром
            for (ParametrizedOperation parametrizedOperation: layer.getParametrizedOperations())
                // обновляем параметр
                parametrizedOperation.setParameter(update(parametrizedOperation.getParameter(),
                        parametrizedOperation.getParameterGradient()));
        }
    }

    /**
     * Вычисление величины снижения скорости обучения
     */
    public void calculateDecayLR() {
        decayLR = (startLR - stopLR) / (epochs - 1);
    }

    /**
     * Снижение скорости обучения
     */
    public void decay() {
        learningRate -= decayLR;
    }

    /**
     * Обновление параметров сети (правило задаётся в наследнике)
     * @param parameters  параметр
     * @param parameterGradients  градиент параметра
     * @return  обновлённый параметр
     */
    protected abstract Matrix update(@NotNull Matrix parameters, @NotNull Matrix parameterGradients);

    @Override
    public abstract Optimizer copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Optimizer optimizer = (Optimizer) o;
        return Double.compare(optimizer.learningRate, learningRate) == 0 &&
               Double.compare(optimizer.decayLR, decayLR) == 0 &&
               Double.compare(optimizer.startLR, startLR) == 0 &&
               Double.compare(optimizer.stopLR, stopLR) == 0 &&
               epochs == optimizer.epochs &&
               Objects.equals(network, optimizer.network);
    }

    @Override
    public int hashCode() {
        return Objects.hash(network, learningRate, decayLR, startLR, stopLR, epochs);
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

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                "нейросеть=" + (network == null ? "null" : network.toString(debugMode)) +
                '}';
    }

    protected abstract String getClassName();

    protected abstract String getDebugClassName();
}
