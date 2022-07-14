package models.losses;

import models.math.Matrix;

public class MeanSquaredError extends Loss{
    @Override
    protected double computeOutput(Matrix prediction, Matrix target) {
        return prediction.sub(target).mul(prediction.sub(target)).sum() / prediction.getRows();
    }

    @Override
    protected Matrix computeInputGradient(Matrix prediction, Matrix target) {
        return prediction.sub(target).mul(2).div(prediction.getRows());
    }

    @Override
    protected String getClassName() {
        return "СреднеквадратическаяОшибка";
    }

    @Override
    protected String getDebugClassName() {
        return "MeanSquaredError";
    }
}
