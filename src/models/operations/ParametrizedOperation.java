package models.operations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;

/**
 * Операция с параметром, наследник {@link Operation}. Параметры модели:
 *  parameter - параметр;
 *  parameterGradient - градиент по параметру.
 */
public abstract class ParametrizedOperation extends Operation {
    protected Matrix parameter;
    protected Matrix parameterGradient;

    /**
     * Конструктор
     * @param parameter параметр
     */
    protected ParametrizedOperation(@NotNull Matrix parameter) {
        super();
        this.parameter = parameter;
    }

    /***
     * copy-constructor
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
    public Matrix backward(@NotNull Matrix outputGradient) {
        this.outputGradient = outputGradient.copy();  // сохраняется копия
        output.assertSameShape(this.outputGradient);  // проверка совпадения размерностей

        inputGradient = computeInputGradient(this.outputGradient);  // вычисление градиента на входе
        input.assertSameShape(inputGradient);  // проверка совпадения размерностей

        parameterGradient = computeParameterGradient(this.outputGradient);  // вычисление градиента по параметру
        parameter.assertSameShape(parameterGradient);  // проверка совпадения размерностей

        return inputGradient;
    }

    /**
     * Очистка промежуточных результатов
     */
    public void clear() {
        super.clear();
        parameterGradient = null;
    }

    /**
     * Вычисление градиента по параметру (реализуется наследниками)
     * @param outputGradient градиент на выходе
     * @return градиент по параметру
     */
    protected abstract Matrix computeParameterGradient(@NotNull Matrix outputGradient);

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
    public abstract ParametrizedOperation copy();

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", outputGradient=" + outputGradient +
                ", inputGradient=" + inputGradient +
                ", parameter=" + parameter +
                ", parameterGradient=" + parameterGradient +
                '}';
    }
}
