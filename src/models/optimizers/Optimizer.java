package models.optimizers;

import models.layers.Layer;
import models.math.Matrix;
import models.networks.Network;
import models.operations.ParametrizedOperation;
import utils.ExceptionUtils;
import utils.copy.CopyUtils;
import utils.copy.DeepCopyable;

/**
 * Оптимизатор, обучающий сеть по определённому правилу. Атрибуты модели:
 *  {@link Network} - сеть для обучения;
 *  learningRate - скорость обучения;
 *  decayLR - величина снижения скорости обучения;
 *  startLR - начальная скорость обучения;
 *  stopLR - конечная скорость обучения;
 *  epochs - длительность обучения;
 */
public abstract class Optimizer implements DeepCopyable {
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
    protected abstract Matrix update(Matrix parameters, Matrix parameterGradients);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "network=" + network +
                ", learningRate=" + learningRate +
                ", decayLR=" + decayLR +
                '}';
    }

    @Override
    public Optimizer deepCopy() {
        return createOptimizer(getClass(), network == null ? null : network.deepCopy(), learningRate, decayLR);
    }

    protected static Optimizer createOptimizer(Class<? extends Optimizer> clazz, Network network,
                                               double learningRate, double decayLR) {
        if (clazz.equals(SGD.class))
            return new SGD(network, learningRate, decayLR);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }

}
