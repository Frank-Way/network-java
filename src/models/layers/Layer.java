package models.layers;

import models.data.approximation.functions.VariableRange;
import models.math.Matrix;
import models.operations.Operation;
import models.operations.ParametrizedOperation;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.CopyUtils;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Слой сети. Представляет собой набор {@link Operation}, которые выполняется при прямом и обратном проходе.
 * Атрибуты модели:
 *  input - входные значения;
 *  output - выходные значения;
 *  neurons - размер слоя;
 *  список<{@link Operation}> - набор операций
 */
@YamlSerializable
public abstract class Layer implements DeepCopyable, Serializable {
//    private static final transient long serialVersionUID = 548499734630114863L;
    protected transient Matrix input;
    protected transient Matrix output;
    @YamlField protected final int neurons;
    @YamlField protected final Operation[] operations;

    protected Layer(Matrix input,
                    Matrix output,
                    int neurons,
                    Operation[] operations) {
        if (neurons <= 0)
            throw new IllegalArgumentException(String.format(
                    "Число нейронов слоя должно быть положительным (получено neurons=%d)", neurons));
        this.input = input;
        this.output = output;
        this.neurons = neurons;
        this.operations = operations;
    }

    /**
     * Прямой проход (вычисление результата)
     * @param input входные значения
     * @return выходные значения
     */
    public Matrix forward(Matrix input) {
        // вычисления производятся с копиями
        this.input = input.deepCopy();
        Matrix result = input.deepCopy();

        for (Operation operation: operations)
            result = operation.forward(result);

        output = result.deepCopy();

        return output;
    }

    /**
     * Обратный проход (вычисление градиентов)
     * @param outputGradient градиент на выходе слоя
     * @return градиент на входе слоя
     */
    public Matrix backward(Matrix outputGradient) {
        Matrix result = outputGradient.deepCopy();
        output.assertSameShape(outputGradient);
        for (int i = 0; i < operations.length; i++)
            // операции берутся в обратном порядке
            result = operations[operations.length - 1 - i].backward(result);
        return result;
    }

    public Matrix getInput() {
        return input;
    }

    public Matrix getOutput() {
        return output;
    }

    public int getNeurons() {
        return neurons;
    }

    /**
     * Получение параметра по классу операции (класс - наследник {@link ParametrizedOperation}). Предполагается, что
     * в слое нет двух операций с одинаковым классом.
     * @param operationClass класс операции
     * @param <T> класс операции
     * @return параметр
     */
    public <T extends ParametrizedOperation> Matrix getParameter(Class<T> operationClass) {
        return Arrays.stream(operations)
                .filter(operation -> operation.getClass().equals(operationClass))  // проверка равенства классов
                .map(operation -> (ParametrizedOperation) operation)  // преобразование к нужному классу
                .findFirst()  // выбор первой операции
                .orElseThrow(() -> new IllegalArgumentException(
                        "Слой не имеет операции типа: " + operationClass.toString()))  // исключение, если нет операций
                .getParameter();  // выбор параметра операции
    }

    private Operation[] getOperations() {
        return operations;
    }

    public Operation getOperation(int index) {
        return operations[index];
    }

    /**
     * Получение операций с параметром
     * @return список операций с параметром
     */
    public ParametrizedOperation[] getParametrizedOperations() {
        return Arrays.stream(operations)
                .filter(operation -> operation instanceof ParametrizedOperation)  // фильтрация по классу
                .map(operation -> (ParametrizedOperation) operation)  // преобразование к нужному типу
                .toArray(ParametrizedOperation[]::new);  // получение операций
    }

    public int operationsCount() {
        return operations.length;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", neurons=" + neurons +
                ", operations=" + Arrays.toString(operations) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layer layer = (Layer) o;
        return neurons == layer.neurons && Objects.equals(input, layer.input) &&
                Objects.equals(output, layer.output) && Arrays.deepEquals(operations, layer.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, neurons, Arrays.hashCode(operations));
    }

    @Override
    public Layer deepCopy() {
        return createLayer(this.getClass(), input == null ? null : input.deepCopy(),
                output == null ? null : output.deepCopy(),
                neurons, Arrays.stream(operations).map(Operation::deepCopy).toArray(Operation[]::new));
    }

    protected static Layer createLayer(Class<? extends Layer> clazz,  Matrix input,  Matrix output, int neurons,  Operation[] operations) {
        if (clazz.equals(DenseLayer.class))
            return new DenseLayer(input, output, neurons, operations);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }
}
