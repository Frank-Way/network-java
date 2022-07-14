package utils;

import models.math.functions.Functions;
import models.math.Matrix;
import models.trainers.FitParameters;
import models.trainers.Trainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RunConfiguration implements Cloneable, Debuggable {
    protected FitParameters fitParameters;
    protected Trainer trainer;
    protected int retries;

    public RunConfiguration(int retries, FitParameters fitParameters, Trainer trainer) {
        this.fitParameters = fitParameters;
        this.trainer = trainer;
        if (retries <= 0)
            throw new IllegalArgumentException(String.format("Недопустимое количество перезапусков (retries=%d)", retries));
        this.retries = retries;
    }

    public FitParameters getFitParameters() {
        return fitParameters;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public int getRetries() {
        return retries;
    }

    public String toTable(double part) {
        List<String> xHeaders = new ArrayList<>();
        List<String> yHeaders = new ArrayList<>();
        Matrix xValues = getFitParameters().getDataset().getValidData().getInputs();
        for (int i = 0; i < xValues.getCols(); i++)
            xHeaders.add("x" + (i + 1));
        Matrix t = getFitParameters().getDataset().getValidData().getOutputs();
        Matrix y = getTrainer().getNetwork().forward(xValues);
        Matrix e = Functions.abs(t.sub(y));
        for (int i = 0; i < t.getCols(); i++) {
            int j = i + 1;
            yHeaders.add("t" + j);
            yHeaders.add("y" + j);
            yHeaders.add("|t" + j + "-y" + j + "|");
        }
        Matrix yValues;
        int col = 0;
        do {
            yValues = t.getCol(col).stack(y.getCol(col), 0).stack(e.getCol(col), 0);
            col++;
        } while (col < t.getCols());
        return Utils.buildStringTable(xHeaders, xValues, yHeaders, yValues, part);
    }

    @Override
    public RunConfiguration clone() {
        try {
            RunConfiguration clone = (RunConfiguration) super.clone();
            clone.fitParameters = fitParameters.clone();
            clone.trainer = trainer.clone();
            clone.retries = retries;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunConfiguration that = (RunConfiguration) o;
        return getRetries() == that.getRetries() && getFitParameters().equals(that.getFitParameters()) && getTrainer().equals(that.getTrainer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFitParameters(), getTrainer(), getRetries());
    }

    @Override
    public String toString() {
        return "RunConfiguration{" +
                "fitParameters=" + fitParameters +
                ", trainer=" + trainer +
                ", retries=" + retries +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "КонфигурацияЗапуска{" +
                "параметрыОбучения=" + fitParameters.toString(debugMode) +
                ", тренер=" + trainer.toString(debugMode) +
                '}';
    }
}
