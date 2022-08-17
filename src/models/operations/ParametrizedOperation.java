package models.operations;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import serialization.YamlSerializationOptions;
import serialization.YamlSerializationUtils;

import java.util.Arrays;
import java.util.List;

import static serialization.YamlSerializationOptions.CRLF;

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

    public String toYaml(int baseIndent, String doubleFormat) {
        StringBuilder sb = new StringBuilder();
        final String baseIndentString = YamlSerializationUtils.repeat(" ", baseIndent);
        sb.append(baseIndentString).append("class: ").append(this.getClass().getCanonicalName()).append(CRLF);
        sb.append(baseIndentString).append("parameter: ").append(CRLF);
        sb.append(parameter.toYaml(baseIndent + YamlSerializationOptions.YAML_INDENT, doubleFormat));
        return sb.toString();
    }

    public static ParametrizedOperation fromYaml(String yaml, int baseIndent) {
        String[] lines = YamlSerializationUtils.removeFirstCharacters(yaml.split(CRLF), yaml.indexOf('c'));
        String cls = YamlSerializationUtils.getClassAsString(lines, 0);
        final String[] patterns = {"class: " + cls.replace(".", "\\.") + "\\n?",
                "parameter:\\s?\\n?"};
        Matrix parameter = Matrix.fromYaml(String.join("\n", YamlSerializationUtils.filterByIndent(lines, 2)),
                YamlSerializationOptions.YAML_INDENT);

        return createParametrizedOperation(cls, parameter);
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

    protected static List<String> getSubclasses() {
        return Arrays.asList(BiasAdd.class.getCanonicalName(), WeightMultiply.class.getCanonicalName());
    }

    protected static ParametrizedOperation createParametrizedOperation(String cls, Matrix parameter) {
        if (cls.equals(BiasAdd.class.getCanonicalName()))
            return new BiasAdd(parameter);
        else if (cls.equals(WeightMultiply.class.getCanonicalName()))
            return new WeightMultiply(parameter);
        else
            throw new IllegalArgumentException("Не известный класс: " + cls);
    }
}
