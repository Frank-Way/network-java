package models.operations;

import models.math.Matrix;

/**
 * Добавление смещения
 */
public class BiasAdd extends ParametrizedOperation {
    /**
     * Конструктор
     * @param bias вектор-столбец смещений
     */
    public BiasAdd(Matrix bias) {
        super(bias);
        if (bias != null && !bias.isCol())
            throw new IllegalArgumentException(String.format(
                    "Размерность матрицы (%d; %d) недопустима для смещения", bias.getRows(), bias.getCols()));
    }

    /**
     * Конструктор для создания глубокой копии экземпляра
     */
    protected BiasAdd(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient, Matrix parameter, Matrix parameterGradient) {
        super(input, output, outputGradient, inputGradient, parameter, parameterGradient);
    }

    /**
     * Конструктор для сериализации
     */
    private BiasAdd() {
        this(null);
    }

    @Override
    protected Matrix computeOutput(Matrix input) {
        return input.addRow(parameter.transpose());  // добавление смещений как строки
    }

    @Override
    protected Matrix computeInputGradient(Matrix outputGradient) {
        return input.onesLike().mulCol(outputGradient);  // суммирование градиента определенным образом
    }

    @Override
    protected Matrix computeParameterGradient(Matrix outputGradient) {
        return outputGradient.sum(1).transpose();  // суммирование градиента по строкам
    }
}
