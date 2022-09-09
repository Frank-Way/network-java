package models.layers;

import models.math.Matrix;
import models.operations.Operation;
import models.operations.ParametrizedOperation;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Слой сети. Представляет собой набор {@link Operation}, которые выполняется при прямом и обратном проходе.
 * Атрибуты модели:
 * <pre><ul>
 *  <li>input                    - входные значения;</li>
 *  <li>output                   - выходные значения;</li>
 *  <li>neurons                  - размер слоя;</li>
 *  <li>набор<{@link Operation}> - набор операций</li>
 * </ul></pre>
 */
@YamlSerializable
public abstract class Layer implements DeepCopyable, Serializable {
    protected transient Matrix input;
    protected transient Matrix output;
    @YamlField protected final int neurons;
    @YamlField protected final Operation[] operations;

    /**
     * Конструктор для создания глубокой копии экземпляра
     */
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
     * @return      выходные значения
     */
    public Matrix forward(Matrix input) {
        // вычисления производятся с копиями
        this.input = input.deepCopy();
        Matrix result = input.deepCopy();

        // вход последовательно идет через все операции слоя
        for (Operation operation: operations)
            result = operation.forward(result);

        output = result.deepCopy();
        return output;
    }

    /**
     * Обратный проход (вычисление градиентов)
     * @param outputGradient градиент на выходе слоя
     * @return               градиент на входе слоя
     */
    public Matrix backward(Matrix outputGradient) {
        // вычисления производятся с копиями
        Matrix result = outputGradient.deepCopy();
        output.assertSameShape(outputGradient);

        // градиент потери идет обратно через операции слоя
        for (int i = 0; i < operations.length; i++)
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

    private Operation[] getOperations() {
        return operations;
    }

    public Operation getOperation(int index) {
        return operations[index];
    }

    public int operationsCount() {
        return operations.length;
    }

    /**
     * Получение параметра по классу операции (класс - наследник {@link ParametrizedOperation}). Предполагается, что
     * в слое нет двух операций с одинаковым классом.
     * @param operationClass класс операции
     * @param <T>            класс операции
     * @return               параметр
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

    /**
     * Получение операций с параметром
     * @return массив операций с параметром
     */
    public ParametrizedOperation[] getParametrizedOperations() {
        return Arrays.stream(operations)
                .filter(operation -> operation instanceof ParametrizedOperation)  // фильтрация по классу
                .map(operation -> (ParametrizedOperation) operation)  // преобразование к нужному типу
                .toArray(ParametrizedOperation[]::new);  // получение операций
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

    /**
     * Создание слоя. Позволяет реализовать копирование слоя в абстрактном классе без дублирования кода в наследниках
     * @param clazz      тип слоя (наследник Layer)
     * @param input      входное значение
     * @param output     выходное значение
     * @param neurons    размер слоя
     * @param operations операции
     * @return           слой с указанными параметрами
     */
    protected static Layer createLayer(Class<? extends Layer> clazz,  Matrix input,  Matrix output, int neurons,  Operation[] operations) {
        if (clazz.equals(DenseLayer.class))
            return new DenseLayer(input, output, neurons, operations);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }
}
