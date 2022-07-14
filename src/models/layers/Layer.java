package models.layers;

import models.math.Matrix;
import models.math.MatrixOperations;
import models.operations.Operation;
import models.operations.ParametrizedOperation;
import utils.Debuggable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class Layer implements Cloneable, Debuggable {
    protected Matrix input;
    protected Matrix output;
    protected int neurons;
    protected List<Matrix> parameters;
    protected List<Matrix> parameterGradients;
    protected List<Operation> operations;

    public Layer(int neurons) {
        if (neurons <= 0)
            throw new IllegalArgumentException(String.format(
                    "Число нейронов слоя должно быть положительным (получено neurons=%d)", neurons));
        this.neurons = neurons;
        operations = new ArrayList<>();
        parameters = new ArrayList<>();
        parameterGradients = new ArrayList<>();
    }

    public Matrix forward(Matrix input) {
        this.input = input.clone();
        Matrix result = input.clone();

        for (Operation operation: operations)
            result = operation.forward(result);

        output = result.clone();

        return output;
    }

    public Matrix backward(Matrix outputGradient) {
        Matrix result = outputGradient.clone();
        MatrixOperations.assertSameShape(output, result);
        parameterGradients.clear();
        Operation operation;
        for (int i = 0; i < operations.size(); i++) {
            operation = operations.get(operations.size() - 1 - i);
            result = operation.backward(result);
            if (operation instanceof ParametrizedOperation)
                addParameterGradient(0, ((ParametrizedOperation) operation).getParameterGradient());
        }

        return result;
    }

    public List<Matrix> getParameters() {
        return parameters;
    }

    public List<Matrix> getParameterGradients() {
        return parameterGradients;
    }

    public void addOperation(Operation operation) {
        getOperations().add(operation);
    }

    public void addParameter(Matrix parameter) {
        getParameters().add(parameter);
    }

    public void addParameter(int index, Matrix parameterGradient) {
        getParameters().add(index, parameterGradient);
    }

    public void addParameterGradient(Matrix parameterGradient) {
        getParameterGradients().add(parameterGradient);
    }

    public void addParameterGradient(int index, Matrix parameterGradient) {
        getParameterGradients().add(index, parameterGradient);
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public Matrix getParameter(int index) {
        return getParameters().get(index);
    }

    public Matrix getParameterGradient(int index) {
        return getParameterGradients().get(index);
    }

    public Operation getOperation(int index) {
        return getOperations().get(index);
    }

    @Override
    public Layer clone() {
        try {
            Layer clone = (Layer) super.clone();
            if (input != null)
                clone.input = input.clone();
            if (output != null)
                clone.output = output.clone();
            clone.neurons = neurons;
            clone.parameters = parameters.stream().map(Matrix::clone).collect(Collectors.toList());
            clone.parameterGradients = parameterGradients.stream().map(Matrix::clone).collect(Collectors.toList());
            clone.operations = operations.stream().map(Operation::clone).collect(Collectors.toList());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Layer layer = (Layer) o;
        return neurons == layer.neurons && Objects.equals(input, layer.input) && Objects.equals(output, layer.output) && Objects.equals(getParameters(), layer.getParameters()) && Objects.equals(getParameterGradients(), layer.getParameterGradients()) && Objects.equals(getOperations(), layer.getOperations());
    }

    @Override
    public int hashCode() {
        return Objects.hash(input, output, neurons, getParameters(), getParameterGradients(), getOperations());
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "input=" + input +
                ", output=" + output +
                ", neurons=" + neurons +
                ", parameters=" + parameters +
                ", parameterGradients=" + parameterGradients +
                ", operations=" + operations +
                '}';
    }

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
