package models.optimizers;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import models.networks.Network;
import utils.Utils;

/**
 * Стохастический градиентный спуск. Наследник {@link Optimizer}.
 */
public class SGD extends Optimizer {
    public SGD(Network network, double learningRate, double decayLR) {
        super(network, learningRate, decayLR);
    }

    @Override
    public Matrix update(@NotNull Matrix parameters, @NotNull Matrix parameterGradients) {
        // из параметра вычитается градиент, домноженный на скорость обучения
        return parameters.sub(parameterGradients.mul(learningRate));
    }

    @Override
    public SGD copy() {
        return new SGD(Utils.copyNullable(network), learningRate, decayLR);
    }

    @Override
    protected String getClassName() {
        return "СтохастическийГрадиентныйСпуск";
    }

    @Override
    protected String getDebugClassName() {
        return "SGD";
    }

    public static class Builder extends Optimizer.Builder {
        public Builder() {
        }

        private Builder(Network network, double learningRate, double decayLR, double startLR, double stopLR, int epochs) {
            super(network, learningRate, decayLR, startLR, stopLR, epochs);
        }

        @Override
        protected Optimizer createObject() {
            return new SGD(network, learningRate, decayLR);
        }

        @Override
        public Optimizer.Builder copy() {
            return new Builder(network, learningRate, decayLR, startLR, stopLR, epochs);
        }
    }
}
