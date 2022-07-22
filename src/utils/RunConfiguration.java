package utils;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.math.functions.Functions;
import models.math.Matrix;
import models.trainers.FitParameters;
import models.trainers.Trainer;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class RunConfiguration implements Copyable<RunConfiguration>, Debuggable {
    private final MyId myId;
    private final FitParameters fitParameters;
    private final Trainer trainer;
    private final int retries;

    public RunConfiguration(int retries, @NotNull FitParameters fitParameters, @NotNull Trainer trainer) {
        this.fitParameters = fitParameters;
        this.trainer = trainer;
        if (retries <= 0)
            throw new IllegalArgumentException(String.format("Недопустимое количество перезапусков (retries=%d)", retries));
        this.retries = retries;

        myId = new MyId(UUID.randomUUID().toString(), null, hashCode() + "");
    }

    /**
     * copy constructor
     */
    private RunConfiguration(MyId myId, FitParameters fitParameters, Trainer trainer, int retries) {
        this.myId = myId;
        this.fitParameters = fitParameters;
        this.trainer = trainer;
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

    public MyId getMyId() {
        return myId;
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
    public RunConfiguration copy() {
        return new RunConfiguration(Utils.copyNullable(myId), Utils.copyNullable(fitParameters),
                Utils.copyNullable(trainer), retries);
    }

    @Override
    public String toString() {
        return "RunConfiguration{" +
                "myId=" + myId +
                ", fitParameters=" + fitParameters +
                ", trainer=" + trainer +
                ", retries=" + retries +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "КонфигурацияЗапуска{" +
                "myId=" + myId +
                ", параметрыОбучения=" + fitParameters.toString(debugMode) +
                ", тренер=" + trainer.toString(debugMode) +
                ", перезапусков=" + retries +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunConfiguration that = (RunConfiguration) o;
        return getRetries() == that.getRetries() &&
               Objects.equals(myId, that.myId) &&
               Objects.equals(getFitParameters(), that.getFitParameters()) &&
               Objects.equals(getTrainer(), that.getTrainer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(fitParameters, trainer, retries);
    }
}