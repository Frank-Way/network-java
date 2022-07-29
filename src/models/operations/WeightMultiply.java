package models.operations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import utils.Utils;

/**
 * Перемножение входов на веса (взвешивание). Наследник {@link ParametrizedOperation}
 */
public class WeightMultiply extends ParametrizedOperation {

    public WeightMultiply(@NotNull Matrix weight) {
        super(weight);
    }

    /**
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
        return new WeightMultiply(Utils.copyNullable(input), Utils.copyNullable(output),
                Utils.copyNullable(outputGradient), Utils.copyNullable(inputGradient),
                Utils.copyNullable(parameter), Utils.copyNullable(parameterGradient));
    }

    @Override
    protected Matrix computeOutput(@NotNull Matrix input) {
        return input.mulMatrix(parameter);  // матричное умножение входа на веса
    }

    @Override
    protected Matrix computeInputGradient(@NotNull Matrix outputGradient) {
        // матричное умножение градиента на транспонированные веса
        return outputGradient.mulMatrix(parameter.transpose());
    }

    @Override
    protected Matrix computeParameterGradient(@NotNull Matrix outputGradient) {
        // матричное умножение транспонированного входа на градиент
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
