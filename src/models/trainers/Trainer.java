package models.trainers;

import models.math.Matrix;
import models.math.MatrixOperations;
import models.networks.Network;
import models.optimizers.Optimizer;
import utils.Debuggable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class Trainer implements Cloneable, Debuggable {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    protected Optimizer optimizer;
    private int earlyStopTriggered;
    private final int abortThreshold = 5;

    public Trainer(Network network, Optimizer optimizer) {
        this.optimizer = optimizer;
        this.optimizer.setNetwork(network);
        earlyStopTriggered = 0;
    }

    public Network getNetwork() {
        return optimizer.getNetwork();
    }

    public FitResults fit(FitParameters parameters) {
        Network network = getNetwork();
        Matrix x;
        Matrix y;
        List<Matrix> xBatches;
        List<Matrix> yBatches;
        double testLoss;
        double trainLoss;
        double bestTestLoss = Double.MAX_VALUE;
        Network bestNetwork = network;
        int queryStep = parameters.epochs / parameters.queries;
        List<Integer> queryAt = new ArrayList<>();
        for (int i = 0; i < parameters.queries; i++)
            queryAt.add(queryStep * (i + 1));
        MyQueue lastTrainLosses = new MyQueue((int)Math.ceil(parameters.queries * 1.0 / 10));
        MyQueue lastTestLosses = new MyQueue((int)Math.ceil(parameters.queries * 1.0 / 10));
        FitResults results = new FitResults();
        optimizer.setEpochs(parameters.epochs);
        optimizer.calculateDecayLR();
        for (int epoch = 1; epoch <= parameters.epochs; epoch++) {
            x = parameters.dataset.getTrainData().getInputs().clone();
            y = parameters.dataset.getTrainData().getOutputs().clone();
            MatrixOperations.shuffleMatrices(x, y);
            xBatches = x.getBatches(parameters.batchSize);
            yBatches = y.getBatches(parameters.batchSize);
            trainLoss = 0.0;
            for (int batch = 0; batch < xBatches.size(); batch++) {
                trainLoss += network.trainBatch(xBatches.get(batch), yBatches.get(batch)) / parameters.batchSize;
                optimizer.step();
            }
            optimizer.decay();
            if (!queryAt.contains(epoch))
                continue;
            x = parameters.dataset.getTestData().getInputs().clone();
            y = parameters.dataset.getTestData().getOutputs().clone();
            MatrixOperations.shuffleMatrices(x, y);
            xBatches = x.getBatches(parameters.batchSize);
            yBatches = y.getBatches(parameters.batchSize);
            testLoss = 0.0;
            for (int batch = 0; batch < xBatches.size(); batch++) {
                testLoss += network.calculateLoss(xBatches.get(batch), yBatches.get(batch)) / parameters.batchSize;
            }
            if (testLoss < bestTestLoss) {
                bestTestLoss = testLoss;
                bestNetwork = network.clone();
            }
            results.getTestLossesMap().put(epoch, testLoss);
            lastTrainLosses.push(trainLoss);
            lastTestLosses.push(testLoss);
            logger.info(String.format("Эпоха: %d, потеря при обучении: " + parameters.doubleFormat + ", потеря при тестах: " +
                    parameters.doubleFormat, epoch, trainLoss, testLoss));
            if (!parameters.earlyStopping)
                continue;
            if (abortTrain(trainLoss, lastTrainLosses, "обучении", parameters.doubleFormat) ||
                    abortTrain(testLoss, lastTestLosses, "тестах", parameters.doubleFormat))
                break;
        }
        logger.fine(String.format("В качестве результата обучения сохранена сеть, обеспечившая потерю на тестовой выборке: " +
                parameters.doubleFormat, bestTestLoss));
        results.setBestNetwork(bestNetwork);
        return results;
    }

    protected boolean abortTrain(double loss, MyQueue lastLosses, String name, String doubleFormat) {
        if (lastLosses.size > 1 && loss >= lastLosses.mean()) {
            logger.warning(String.format("Потеря при %s " + doubleFormat + " превышает последние %d потерь (" + doubleFormat + ")",
                    name, loss, lastLosses.size, lastLosses.mean()));
            if (++earlyStopTriggered >= abortThreshold) {
                logger.info(String.format("Ранняя остановка сработала %d раз подряд. Завершение обучения",
                        earlyStopTriggered));
                return true;
            }
        }
        else
            earlyStopTriggered = 0;
        return false;
    }

    @Override
    public Trainer clone() {
        try {
            Trainer clone = (Trainer) super.clone();
            clone.optimizer = optimizer.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trainer trainer = (Trainer) o;
        return optimizer.equals(trainer.optimizer);
    }

    @Override
    public int hashCode() {
        return Objects.hash(optimizer);
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "optimizer=" + optimizer +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                "оптимизатор=" + optimizer.toString(debugMode) +
                '}';
    }

    protected String getClassName() {
        return "Тренер";
    }

    protected String getDebugClassName() {
        return "Trainer";
    }
}
