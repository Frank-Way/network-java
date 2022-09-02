package models.operations;

import models.math.Matrix;

/**
 * Перемножение входов на веса (взвешивание). Наследник {@link ParametrizedOperation}
 */
public class WeightMultiply extends ParametrizedOperation {
    /**
     * Конструктор
     * @param weight матрица весовых коэффициентов
     */
    public WeightMultiply(Matrix weight) {
        super(weight);
    }

    /**
     * Конструктор для создания глубокой копии экземпляра
     */
    protected WeightMultiply(Matrix input,
                             Matrix output,
                             Matrix outputGradient,
                             Matrix inputGradient,
                             Matrix parameter,
                             Matrix parameterGradient) {
        super(input, output, outputGradient, inputGradient, parameter, parameterGradient);
    }

    /**
     * Конструктор для сериализации
     */
    private WeightMultiply() {
        this(null);
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return input.mulMatrix(parameter);  // матричное умножение входа на веса
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        // матричное умножение градиента на транспонированные веса
        return outputGradient.mulMatrix(parameter.transpose());
    }

    @Override
    protected Matrix computeParameterGradient(Matrix outputGradient) {
        // матричное умножение транспонированного входа на градиент
        return input.transpose().mulMatrix(outputGradient);
    }
}
