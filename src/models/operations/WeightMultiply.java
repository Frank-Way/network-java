package models.operations;

import models.math.Matrix;

public class WeightMultiply extends ParametrizedOperation {
    public WeightMultiply(Matrix weight) {
        super(weight);
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return input.mulMatrix(parameter);
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        return outputGradient.mulMatrix(parameter.transpose());
    }

    @Override
    protected Matrix computeParameterGradient(Matrix outputGradient) {
        return input.transpose().mulMatrix(outputGradient);
    }

    @Override
    protected String getClassName() {
        return "Взвешивание";
    }

    @Override
    protected String getDebugClassName() {
        return "WeightMultiply";
    }
}
