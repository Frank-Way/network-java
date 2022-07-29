package models.data;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.math.Matrix;
import utils.Utils;

import java.util.Objects;

public class Data implements Copyable<Data> {
    private final Matrix inputs;
    private final Matrix outputs;

    public Data(@NotNull Matrix inputs, @NotNull Matrix outputs) {
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
    public Data copy() {
        return new Data(Utils.copyNullable(inputs), Utils.copyNullable(outputs));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Data data = (Data) o;
        return Objects.equals(getInputs(), data.getInputs()) &&
               Objects.equals(getOutputs(), data.getOutputs());
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
