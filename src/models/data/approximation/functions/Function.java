package models.data.approximation.functions;

import models.data.approximation.functions.impl.*;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Описание функции, для воспроизведения которого будет обучена сеть. Функция может быть многомерной (при этом выход
 * всегда один). Функция должна быть гладкой. Контроль за указанием правильных значений (на которых функция определена)
 * остается за пользователем. Атрибуты модели:
 * <pre><ul>
 *  <li>expression                   - строковое представление функции;</li>
 *  <li>набор<{@link VariableRange}> - набор диапазонов изменения каждой входной переменной.</li>
 * </ul></pre>
 */
@YamlSerializable
public abstract class Function implements DeepCopyable, Serializable {
    @YamlField protected final String expression;
    @YamlField protected final VariableRange[] variableRanges;

    /**
     * Конструктор, cм. описание параметров в {@link Function}
     */
    protected Function(String expression, VariableRange[] variableRanges) {
        this.expression = expression;
        this.variableRanges = variableRanges;
    }

    public int getInputsCount() {
        return variableRanges.length;
    }

    public String getExpression() {
        return expression;
    }

    public VariableRange[] getVariableRanges() {
        return variableRanges;
    }

    // логика вычислений определяется наследниками
    public abstract double calculate(double ... arguments);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "expression='" + expression + '\'' +
                ", variableRanges=" + Arrays.toString(variableRanges) +
                '}';
    }

    @Override
    public abstract Function deepCopy();

    private static Function createFunction(Class<?> type, VariableRange[] variableRanges) {
        if (type.equals(Sin2X.class))
            return new Sin2X(variableRanges);
        else if (type.equals(SinX.class))
            return new SinX(variableRanges);
        else if (type.equals(SinX1_mul_X2.class))
            return new SinX1_mul_X2(variableRanges);
        else if (type.equals(X.class))
            return new X(variableRanges);
        else if (type.equals(XOR.class))
            return new XOR(variableRanges);
        throw ExceptionUtils.newUnknownClassException(type);
    }
}
