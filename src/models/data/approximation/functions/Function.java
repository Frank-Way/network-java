package models.data.approximation.functions;

import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
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
}
