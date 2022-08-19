package models.data.approximation.functions;

import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Описание функции, для воспроизведения которого будет обучена сеть. Функция может быть многомерной (при этом выход ё
 * всегда один). Функция должна быть гладкой. Контроль за указанием правильных значений (на которых функция определена)
 * остается за пользователем. Атрибуты модели:
 *  expression - строковое представление функции;
 *  список<{@link VariableRange}> - набор диапазонов изменения каждой входной переменной.
 */
@YamlSerializable
public abstract class Function implements DeepCopyable, Serializable {
    @YamlField protected final String expression;
    @YamlField protected final VariableRange[] variableRanges;

    /**
     * Конструктор
     * @param expression строковое представление
     * @param variableRanges диапазоны входных переменных
     */
    public Function(String expression, VariableRange[] variableRanges) {
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
    public abstract double calculate(double[] arguments);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "expression='" + expression + '\'' +
                ", variableRanges=" + Arrays.toString(variableRanges) +
                '}';
    }

    protected static VariableRange[] getDefaultVariableRanges() {
        throw new NotImplementedException();
    }

    @Override
    public abstract Function deepCopy();
}
