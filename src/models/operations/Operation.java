package models.operations;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.math.Matrix;

import java.io.Serializable;

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

    /**
     * Очистка промежуточных результатов
     */
    public void clear() {
        input = null;
        output = null;
        outputGradient = null;
        inputGradient = null;
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
}
