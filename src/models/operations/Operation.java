package models.operations;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.math.Matrix;
import models.operations.activations.Linear;
import models.operations.activations.Sigmoid;
import models.operations.activations.Tanh;
import serialization.YamlSerializationOptions;
import serialization.YamlSerializationUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import static serialization.YamlSerializationOptions.CRLF;

/**
 * Операция выполняемая в слое сети. Параметры модели:
 *  input - входное значение;
 *  output - выходное значение;
 *  outputGradient - градиент на выходе (входное значение при обратном проходе);
 *  inputGradient - градиент на выходе (выходное значение при обратном проходе).
 */
public abstract class Operation implements Copyable<Operation>, Debuggable, Serializable {
    private static final long serialVersionUID = 2002727109271183922L;
    protected Matrix input;
    protected Matrix output;
    protected Matrix outputGradient;
    protected Matrix inputGradient;

    /**
     * Пустой конструктор
     */
    public Operation() {}

    /***
     * copy-constructor
     */
    protected Operation(Matrix input, Matrix output, Matrix outputGradient, Matrix inputGradient) {
        this.input = input;
        this.output = output;
        this.outputGradient = outputGradient;
        this.inputGradient = inputGradient;
    }

    /**
     * Прямой проход (вычисление выхода)
     * @param input вход
     * @return выход
     */
    public Matrix forward(@NotNull Matrix input) {
        this.input = input.copy();  // сохраняется копия
        output = computeOutput(this.input);  // вычисление выполнятся с копией
        return output;
    }

    /**
     * Обратный проход (вычисление градиента)
     * @param outputGradient градиент на выходе
     * @return градиент на входе
     */
    public Matrix backward(@NotNull Matrix outputGradient) {
        this.outputGradient = outputGradient.copy();  // сохраняется копия
        output.assertSameShape(this.outputGradient);  // проверка совпадения размерностей

        inputGradient = computeInputGradient(this.outputGradient); // вычисление
        input.assertSameShape(inputGradient);  // проверка совпадения размерностей

        return inputGradient;
    }

    public String toYaml(int baseIndent, String doubleFormat) {
        StringBuilder sb = new StringBuilder();
        final String baseIndentString = YamlSerializationUtils.repeat(" ", baseIndent);
        sb.append(baseIndentString).append("class: ").append(this.getClass().getCanonicalName()).append(CRLF);
        return sb.toString();
    }

    public static Operation fromYaml(String yaml, int baseIndent) {
        String[] lines = YamlSerializationUtils.removeFirstCharacters(yaml.split(CRLF), yaml.indexOf('c'));
        String cls = YamlSerializationUtils.getClassAsString(lines, 0);
        if (ParametrizedOperation.getSubclasses().contains(cls))
            return ParametrizedOperation.fromYaml(yaml, baseIndent);
        final String pattern = "(\\" + YamlSerializationOptions.YAML_LIST_PREFIX + ")?class: " + cls.replace(".", "\\.") + "\\n?";
        if (lines.length != 1 || !lines[0].matches(pattern))
            throw new IllegalArgumentException("Не верный формат строки: " + yaml);
        return createOperation(cls);
    }

    /**
     * Вычисление выхода (определяется наследником)
     * @param input вход
     * @return выход
     */
    protected abstract Matrix computeOutput(@NotNull Matrix input);

    /**
     * Вычисление градиента (определяется наследником)
     * @param outputGradient градиент на выходе
     * @return градиент на входе
     */
    protected abstract Matrix computeInputGradient(@NotNull Matrix outputGradient);

    @Override
    public abstract Operation copy();

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", outputGradient=" + outputGradient +
                ", inputGradient=" + inputGradient +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                '}';
    }

    protected abstract String getClassName();

    protected abstract String getDebugClassName();

    protected static List<String> getSubclasses() {
        return Arrays.asList(Linear.class.getCanonicalName(), Sigmoid.class.getCanonicalName(),
                Tanh.class.getCanonicalName());
    }

    protected static Operation createOperation(String cls) {
        if (cls.equals(Linear.class.getCanonicalName()))
            return new Linear();
        else if (cls.equals(Sigmoid.class.getCanonicalName()))
            return new Sigmoid();
        else if (cls.equals(Tanh.class.getCanonicalName()))
            return new Tanh();
        else
            throw new IllegalArgumentException("Не известный класс: " + cls);
    }
}
