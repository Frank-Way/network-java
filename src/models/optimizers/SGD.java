package models.optimizers;

import models.math.Matrix;
import models.networks.Network;

/**
 * Стохастический градиентный спуск. Наследник {@link Optimizer}.
 */
public class SGD extends Optimizer {
    public SGD(Network network, double learningRate, double decayLR) {
        super(network, learningRate, decayLR);
    }

    private SGD() {
        this(null, 0, 0);
    }

    @Override
    protected Matrix update(Matrix parameters, Matrix parameterGradients) {
        // из параметра вычитается градиент, домноженный на скорость обучения
        return parameters.sub(parameterGradients.mul(learningRate));
    }

    public static SGDBuilder builder() {
        return new SGDBuilder();
    }

}
