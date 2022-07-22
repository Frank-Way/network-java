package models.losses;

import models.math.Matrix;
import utils.Utils;

public class MeanSquaredError extends Loss{
    public MeanSquaredError() {super();}
    /***
     * copy-constructor
     */
    private MeanSquaredError(Matrix prediction,
                            Matrix target,
                            double output,
                            Matrix inputGradient) {
        super(prediction, target, output, inputGradient);
    }

    @Override
    protected double computeOutput(Matrix prediction, Matrix target) {
        return prediction.sub(target).mul(prediction.sub(target)).sum() / prediction.getRows();
    }

    @Override
    protected Matrix computeInputGradient(Matrix prediction, Matrix target) {
        return prediction.sub(target).mul(2).div(prediction.getRows());
    }

    @Override
    public MeanSquaredError copy() {
        Matrix predictionCopy = Utils.copyNullable(prediction);
        Matrix targetCopy = Utils.copyNullable(target);
        Matrix inputGradientCopy = Utils.copyNullable(inputGradient);

        return new MeanSquaredError(predictionCopy, targetCopy, output, inputGradientCopy);
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
