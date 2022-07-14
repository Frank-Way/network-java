package models.data;

import models.math.Matrix;

import java.util.Objects;

public class Data implements Cloneable {
    protected Matrix inputs;
    protected Matrix outputs;

    public Data(Matrix inputs, Matrix outputs) {
        if (inputs.getRows() != outputs.getRows())
            throw new IllegalArgumentException(String.format(
                    "Количество строк выборки не совпадает для входов (%d; %d) и выходов (%d; %d)",
                    inputs.getRows(), inputs.getCols(), outputs.getRows(), outputs.getCols()));
        this.inputs = inputs;
        this.outputs = outputs;
    }

    public Matrix getInputs() {
        return inputs;
    }

    public Matrix getOutputs() {
        return outputs;
    }

    public int getRows() {
        return inputs.getRows();
    }

    @Override
    public Data clone() {
        try {
            Data clone = (Data) super.clone();
            clone.inputs = inputs.clone();
            clone.outputs = outputs.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return getInputs().equals(data.getInputs()) && getOutputs().equals(data.getOutputs());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInputs(), getOutputs());
    }

    @Override
    public String toString() {
        return "Data{" +
                "inputs=" + inputs +
                ", outputs=" + outputs +
                '}';
    }
}
