package models.operations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import utils.Utils;

public class WeightMultiply extends ParametrizedOperation {
    public WeightMultiply(@NotNull Matrix weight) {
        super(weight);
    }

    /***
     * copy-constructor
     */
    private WeightMultiply(Matrix input,
                          Matrix output,
                          Matrix outputGradient,
                          Matrix inputGradient,
                          Matrix parameter,
                          Matrix parameterGradient) {
        super(input, output, outputGradient, inputGradient, parameter, parameterGradient);
    }

    @Override
    public WeightMultiply copy() {
        Matrix inputCopy = Utils.copyNullable(input);
        Matrix outputCopy = Utils.copyNullable(output);
        Matrix outputGradientCopy = Utils.copyNullable(outputGradient);
        Matrix inputGradientCopy = Utils.copyNullable(inputGradient);
        Matrix parameterCopy = Utils.copyNullable(parameter);
        Matrix parameterGradientCopy = Utils.copyNullable(parameterGradient);
        
        return new WeightMultiply(inputCopy, outputCopy, outputGradientCopy, inputGradientCopy, 
                parameterCopy, parameterGradientCopy);
    }

    @Override
    protected Matrix computeOutput(@NotNull Matrix input) {
        return input.mulMatrix(parameter);
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        return outputGradient.mulMatrix(parameter.transpose());
    }

    @Override
    protected Matrix computeParameterGradient(@NotNull Matrix outputGradient) {
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
