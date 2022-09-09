package models.optimizers;

import models.layers.Layer;
import models.math.Matrix;
import models.networks.Network;
import models.operations.ParametrizedOperation;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

/**
 * Оптимизатор, обучающий сеть по определённому правилу. Атрибуты модели:
 * <pre><ul>
 *  <li>{@link Network} - сеть для обучения;</li>
 *  <li>learningRate    - скорость обучения;</li>
 *  <li>decayLR         - величина снижения скорости обучения;</li>
 * </ul></pre>
 */
public abstract class Optimizer implements DeepCopyable {
    protected final Network network;
    protected double learningRate;
    protected final double decayLR;

    /**
     * Конструктор, см. описание параметров в {@link Optimizer}
     */
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
     * @param parameters         параметр
     * @param parameterGradients градиент параметра
     * @return                   обновлённый параметр
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

    /**
     * Создание оптимизатора
     * @param clazz        тип оптимизатора
     * @param network      сеть
     * @param learningRate скорость обучения
     * @param decayLR      величина снижения скорости обучения
     * @return             оптимизатор с заданными параметрами
     */
    protected static Optimizer createOptimizer(Class<? extends Optimizer> clazz, Network network,
                                               double learningRate, double decayLR) {
        if (clazz.equals(SGD.class))
            return new SGD(network, learningRate, decayLR);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }

}
