package models.optimizers;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import models.networks.Network;
import utils.Utils;

/**
 * Стохастический градиентный спуск. Наследник {@link Optimizer}.
 */
public class SGD extends Optimizer {
    public SGD(double startLR, double stopLR) {
        super(startLR, stopLR);
    }

    /***
     * copy-constructor
     */
    private SGD(Network network,
               double learningRate,
               double decayLR,
               double startLR,
               double stopLR,
               int epochs) {
        super(network, learningRate, decayLR, startLR, stopLR, epochs);
    }

    @Override
    public Matrix update(@NotNull Matrix parameters, @NotNull Matrix parameterGradients) {
        // из параметра вычитается градиент, домноженный на скорость обучения
        return parameters.sub(parameterGradients.mul(learningRate));
    }

    @Override
    public SGD copy() {
        return new SGD(Utils.copyNullable(network), learningRate, decayLR, startLR, stopLR, epochs);
    }

    @Override
    protected String getClassName() {
        return "СтохастическийГрадиентныйСпуск";
    }

    @Override
    protected String getDebugClassName() {
        return "SGD";
    }
}
