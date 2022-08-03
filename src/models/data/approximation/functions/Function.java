package models.data.approximation.functions;

import com.sun.istack.internal.NotNull;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

/**
 * Описание функции, для воспроизведения которого будет обучена сеть. Функция может быть многомерной (при этом выход ё
 * всегда один). Функция должна быть гладкой. Контроль за указанием правильных значений (на которых функция определена)
 * остается за пользователем. Атрибуты модели:
 *  expression - строковое представление функции;
 *  список<{@link VariableRange}> - набор диапазонов изменения каждой входной переменной.
 */
public abstract class Function {
    private final String expression;
    private final List<VariableRange> variableRanges;

    /**
     * Конструктор
     * @param expression строковое представление
     * @param variableRanges диапазоны входных переменных
     */
    public Function(@NotNull String expression, @NotNull List<VariableRange> variableRanges) {
        this.expression = expression;
        this.variableRanges = variableRanges;
    }

    public int getInputsCount() {
        return variableRanges.size();
    }

    public String getExpression() {
        return expression;
    }

    public List<VariableRange> getVariableRanges() {
        return variableRanges;
    }

    // логика вычислений определяется наследниками
    public abstract double calculate(@NotNull double[] arguments);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "expression='" + expression + '\'' +
                ", variableRanges=" + variableRanges +
                '}';
    }

    protected static List<VariableRange> getDefaultVariableRanges() {
        throw new NotImplementedException();
    }
}
