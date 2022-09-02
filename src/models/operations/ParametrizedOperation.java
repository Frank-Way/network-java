package models.operations;

import models.math.Matrix;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;

import java.util.Objects;

/**
 * Операция с параметром, наследник {@link Operation}. Параметры модели:
 * <pre><ul>
 *  <li>parameter         - параметр;</li>
 *  <li>parameterGradient - градиент по параметру.</li>
 * </ul></pre>
 */
@YamlSerializable
public abstract class ParametrizedOperation extends Operation {
    @YamlField protected Matrix parameter;
    protected transient Matrix parameterGradient;

    /**
     * Конструктор
     * @param parameter параметр
     */
    protected ParametrizedOperation(Matrix parameter) {
        super();
        this.parameter = parameter;
    }

    /**
     * Конструктор для создания глубокой копии экземпляра
     */
    protected ParametrizedOperation(Matrix input,
                                    Matrix output,
                                    Matrix outputGradient,
                                    Matrix inputGradient,
                                    Matrix parameter,
                                    Matrix parameterGradient) {
        super(input, output, outputGradient, inputGradient);
        this.parameter = parameter;
        this.parameterGradient = parameterGradient;
    }

    @Override
    public Matrix backward(Matrix outputGradient) {
        this.outputGradient = outputGradient.deepCopy();  // сохраняется копия
        output.assertSameShape(this.outputGradient);  // проверка совпадения размерностей

        inputGradient = computeInputGradient(this.outputGradient);  // вычисление градиента на входе
        input.assertSameShape(inputGradient);  // проверка совпадения размерностей

        parameterGradient = computeParameterGradient(this.outputGradient);  // вычисление градиента по параметру
        parameter.assertSameShape(parameterGradient);  // проверка совпадения размерностей

        return inputGradient;
    }

    /**
     * Вычисление градиента по параметру (реализуется наследниками)
     * @param outputGradient градиент на выходе
     * @return градиент по параметру
     */
    protected abstract Matrix computeParameterGradient(Matrix outputGradient);

    public Matrix getParameter() {
        return parameter;
    }

    public Matrix getParameterGradient() {
        return parameterGradient;
    }

    public void setParameter(Matrix parameter) {
        this.parameter = parameter;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", outputGradient=" + outputGradient +
                ", inputGradient=" + inputGradient +
                ", parameter=" + parameter +
                ", parameterGradient=" + parameterGradient +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ParametrizedOperation that = (ParametrizedOperation) o;
        return parameter.equals(that.parameter) && Objects.equals(parameterGradient, that.parameterGradient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parameter, parameterGradient);
    }

    @Override
    public ParametrizedOperation deepCopy() {
        return createParametrizedOperation(getClass(),
                input == null ? null : input.deepCopy(),
                output == null ? null : output.deepCopy(),
                outputGradient == null ? null : outputGradient.deepCopy(),
                inputGradient == null ? null : inputGradient.deepCopy(),
                parameter.deepCopy(),
                parameterGradient == null ? null : parameterGradient.deepCopy());
    }

    /**
     * Создание операции с параметром
     * @param clazz             тип операции
     * @param input             входные значения
     * @param output            выходные значения
     * @param outputGradient    градиент на выходе
     * @param inputGradient     градиент на входе
     * @param parameter         параметр
     * @param parameterGradient градиент по параметру
     * @return                  операция с заданными параметрами
     */
    protected static ParametrizedOperation createParametrizedOperation(Class<? extends ParametrizedOperation> clazz,
                                                                       Matrix input,
                                                                       Matrix output,
                                                                       Matrix outputGradient,
                                                                       Matrix inputGradient,
                                                                       Matrix parameter,
                                                                       Matrix parameterGradient) {
        if (clazz.equals(WeightMultiply.class))
            return new WeightMultiply(input, output, outputGradient, inputGradient, parameter, parameterGradient);
        if (clazz.equals(BiasAdd.class))
            return new BiasAdd(input, output, outputGradient, inputGradient, parameter, parameterGradient);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }
}
