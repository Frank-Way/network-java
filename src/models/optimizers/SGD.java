package models.optimizers;

import models.math.Matrix;
import models.networks.Network;

/**
 * Стохастический градиентный спуск. Наследник {@link Optimizer}.
 */
public class SGD extends Optimizer {
    /**
     * Конструктор
     * @param network      сеть
     * @param learningRate скорость обучения
     * @param decayLR      величина снижения скорости обучения
     */
    public SGD(Network network, double learningRate, double decayLR) {
        super(network, learningRate, decayLR);
    }

    /**
     * Конструктор для сериализации
     */
    private SGD() {
        this(null, 0, 0);
    }

    @Override
    protected Matrix update(Matrix parameters, Matrix parameterGradients) {
        // из параметра вычитается градиент, домноженный на скорость обучения
        return parameters.sub(parameterGradients.mul(learningRate));
    }

    /**
     * Получение пустого билдера
     * @return новый билдер
     */
    public static SGDBuilder newBuilder() {
        return new SGDBuilder();
    }

    /**
     * Получение билдера на основе текущего экземпляра
     * @return заполненный билдер
     */
    public SGDBuilder builder() {
        SGDBuilder builder = new SGDBuilder();
        builder.network(network);
        return builder;
    }

}
