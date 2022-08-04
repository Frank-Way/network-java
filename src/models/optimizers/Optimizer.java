package models.optimizers;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.layers.Layer;
import models.math.Matrix;
import models.networks.Network;
import models.operations.ParametrizedOperation;

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
    protected final Network network;
    protected double learningRate;
    protected final double decayLR;

    public Optimizer(Network network, double learningRate, double decayLR) {
        this.network = network;
        this.learningRate = learningRate;
        this.decayLR = decayLR;
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
    public String toString() {
        return getDebugClassName() + "{" +
                "network=" + network +
                ", learningRate=" + learningRate +
                ", decayLR=" + decayLR +
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

    /**
     * Билдер для оптимизаторов
     */
    public abstract static class Builder implements Copyable<Builder> {
        protected Network network;
        protected double learningRate;
        protected double decayLR;
        protected double startLR;
        protected double stopLR;
        protected int epochs;

        public Builder() {};

        protected Builder(Network network, double learningRate, double decayLR, double startLR, double stopLR, int epochs) {
            this.network = network;
            this.learningRate = learningRate;
            this.decayLR = decayLR;
            this.startLR = startLR;
            this.stopLR = stopLR;
            this.epochs = epochs;
        }

        public Builder network(Network network) {
            this.network = network;
            return this;
        }

        public Builder startLR(double startLR) {
            this.learningRate = startLR;
            this.startLR = startLR;
            return this;
        }

        public Builder stopLR(double stopLR) {
            this.stopLR = stopLR;
            return this;
        }

        public Builder epochs(int epochs) {
            this.epochs = epochs;
            return this;
        }

        public Optimizer build() {
            validate();
            calculateDecayLR();
            return createObject();
        }

        protected abstract Optimizer createObject();

        @Override
        public abstract Builder copy();

        private void validate() {
            if (network == null)
                throw new IllegalStateException("Не задана сеть для построения оптимизатора");
            if (startLR <= stopLR)
                throw new IllegalStateException("Неверно заданы начальная и конечная скорости обучения");
        }

        private void calculateDecayLR() {
            decayLR = (startLR - stopLR) / (epochs - 1);
        }
    }
}
