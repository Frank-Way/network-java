package models.optimizers;

import models.math.Matrix;

public class SGD extends Optimizer {
    public SGD(double startLR, double stopLR) {
        super(startLR, stopLR);
    }

    @Override
    public Matrix update(Matrix parameters, Matrix parameterGradients) {
        return parameters.sub(parameterGradients.mul(learningRate));
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
