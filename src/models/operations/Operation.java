package models.operations;

import models.math.Matrix;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Операция выполняемая в слое сети. Параметры модели:
 * <pre><ul>
 *  <li>input          - входное значение;</li>
 *  <li>output         - выходное значение;</li>
 *  <li>outputGradient - градиент на выходе (входное значение при обратном проходе);</li>
 *  <li>inputGradient  - градиент на выходе (выходное значение при обратном проходе).</li>
 * </ul></pre>
 */
@YamlSerializable
public abstract class Operation implements DeepCopyable, Serializable {
    protected transient Matrix input;
    protected transient Matrix output;
    protected transient Matrix outputGradient;
    protected transient Matrix inputGradient;

    /**
     * Конструктор
     */
    public Operation() {}

    /**
     * Конструктор для создания глубокой копии экземпляра
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
     * @return      выход
     */
    public Matrix forward(Matrix input) {
        this.input = input.deepCopy();  // сохраняется копия
        output = computeOutput(this.input);  // вычисление выполнятся с копией
        return output;
    }

    /**
     * Обратный проход (вычисление градиента)
     * @param outputGradient градиент на выходе
     * @return               градиент на входе
     */
    public Matrix backward(Matrix outputGradient) {
        this.outputGradient = outputGradient.deepCopy();  // сохраняется копия
        output.assertSameShape(this.outputGradient);  // проверка совпадения размерностей

        inputGradient = computeInputGradient(this.outputGradient); // вычисление
        input.assertSameShape(inputGradient);  // проверка совпадения размерностей

        return inputGradient;
    }

    /**
     * Вычисление выхода (определяется наследником)
     * @param input вход
     * @return      выход
     */
    protected abstract Matrix computeOutput(Matrix input);

    /**
     * Вычисление градиента (определяется наследником)
     * @param outputGradient градиент на выходе
     * @return               градиент на входе
     */
    protected abstract Matrix computeInputGradient(Matrix outputGradient);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", outputGradient=" + outputGradient +
                ", inputGradient=" + inputGradient +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Operation operation = (Operation) o;
        return Objects.equals(input, operation.input) && Objects.equals(output, operation.output)
                && Objects.equals(outputGradient, operation.outputGradient)
                && Objects.equals(inputGradient, operation.inputGradient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, outputGradient, inputGradient);
    }

    @Override
    public Operation deepCopy() {
//        if (getClass().getSuperclass().equals(ParametrizedOperation.class))
//            return ((ParametrizedOperation) this).deepCopy();
        return createOperation(getClass(), input == null ? null : input.deepCopy(),
                output == null ? null : output.deepCopy(),
                outputGradient == null ? null : outputGradient.deepCopy(),
                inputGradient == null ? null : inputGradient.deepCopy());
    }

    /**
     * Создание операции
     * @param clazz          тип операции
     * @param input          входные значения
     * @param output         выходные значения
     * @param outputGradient градиент на выходе
     * @param inputGradient  градиент на входе
     * @return               операция с заданными параметрами
     */
    protected static Operation createOperation(Class<? extends Operation> clazz, Matrix input, Matrix output,
                                               Matrix outputGradient, Matrix inputGradient) {
        if (clazz.equals(LinearActivation.class))
            return new LinearActivation(input, output, outputGradient, inputGradient);
        if (clazz.equals(SigmoidActivation.class))
            return new SigmoidActivation(input, output, outputGradient, inputGradient);
        if (clazz.equals(TanhActivation.class))
            return new TanhActivation(input, output, outputGradient, inputGradient);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }
}
