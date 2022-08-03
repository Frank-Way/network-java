package models.layers;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.math.Matrix;
import models.operations.Operation;
import models.operations.ParametrizedOperation;
import models.interfaces.Debuggable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Слой сети. Представляет собой набор {@link Operation}, которые выполняется при прямом и обратном проходе.
 * Атрибуты модели:
 *  input - входные значения;
 *  output - выходные значения;
 *  neurons - размер слоя;
 *  список<{@link Operation}> - набор операций
 */
public abstract class Layer implements Copyable<Layer>, Debuggable, Serializable {
    protected Matrix input;
    protected Matrix output;
    protected final int neurons;
    protected final List<Operation> operations;

    /**
     * Конструктор. Проверяется положительность размера слоя, при нарушении выбрасывается исключение.
     * @param neurons размер слоя
     */
    public Layer(int neurons) {
        if (neurons <= 0)
            throw new IllegalArgumentException(String.format(
                    "Число нейронов слоя должно быть положительным (получено neurons=%d)", neurons));
        this.neurons = neurons;
        operations = new ArrayList<>();
    }

    /**
     * copy-constructor
     */
    protected Layer(Matrix input,
                    Matrix output,
                    int neurons,
                    List<Operation> operations) {
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
    public Matrix forward(@NotNull Matrix input) {
        // вычисления производятся с копиями
        this.input = input.copy();
        Matrix result = input.copy();

        for (Operation operation: operations)
            result = operation.forward(result);

        output = result.copy();

        return output;
    }

    /**
     * Обратный проход (вычисление градиентов)
     * @param outputGradient градиент на выходе слоя
     * @return градиент на входе слоя
     */
    public Matrix backward(@NotNull Matrix outputGradient) {
        Matrix result = outputGradient.copy();
        output.assertSameShape(outputGradient);
        for (int i = 0; i < operations.size(); i++)
            // операции берутся в обратном порядке
            result = operations.get(operations.size() - 1 - i).backward(result);
        return result;
    }

    /**
     * Очистка промежуточных результатов
     */
    public void clear() {
        input = null;
        output = null;
        operations.forEach(Operation::clear);
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
        return operations.stream()
                .filter(operation -> operation.getClass().equals(operationClass))  // проверка равенства классов
                .map(operation -> (ParametrizedOperation) operation)  // преобразование к нужному классу
                .findFirst()  // выбор первой операции
                .orElseThrow(() -> new IllegalArgumentException(
                        "Слой не имеет операции типа: " + operationClass.toString()))  // исключение, если нет операций
                .getParameter();  // выбор параметра операции
    }

    private List<Operation> getOperations() {
        return operations;
    }

    public Operation getOperation(int index) {
        return operations.get(index);
    }

    /**
     * Получение операций с параметром
     * @return список операций с параметром
     */
    public List<ParametrizedOperation> getParametrizedOperations() {
        return operations.stream()
                .filter(operation -> operation instanceof ParametrizedOperation)  // фильтрация по классу
                .map(operation -> (ParametrizedOperation) operation)  // преобразование к нужному типу
                .collect(Collectors.toList());  // получение списка операций
    }

    public void addOperation(@NotNull Operation operation) {
        operations.add(operation);
    }

    public void addOperation(int index, @NotNull Operation operation) {
        operations.add(index, operation);
    }

    public int operationsCount() {
        return operations.size();
    }

    @Override
    public abstract Layer copy();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layer layer = (Layer) o;
        return neurons == layer.neurons &&
               Objects.equals(input, layer.input) &&
               Objects.equals(output, layer.output) &&
               Objects.equals(operations, layer.operations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, neurons, operations);
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", neurons=" + neurons +
                ", operations=" + operations +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                "нейронов=" + neurons +
                ", операции=" + operations.stream().map(operation -> operation.toString(debugMode)).collect(Collectors.toList()) +
                '}';
    }

    protected abstract String getClassName();

    protected abstract String getDebugClassName();
}
