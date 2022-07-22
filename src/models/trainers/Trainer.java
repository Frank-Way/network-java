package models.trainers;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.math.Matrix;
import models.math.MatrixOperations;
import models.networks.Network;
import models.optimizers.Optimizer;
import models.interfaces.Debuggable;
import utils.Utils;

import java.util.*;
import java.util.logging.Logger;

public class Trainer implements Copyable<Trainer>, Debuggable {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final Optimizer optimizer;
    private final int ABORT_THRESHOLD = 5;

    public Trainer(@NotNull Network network, @NotNull Optimizer optimizer) {
        this.optimizer = optimizer;
        this.optimizer.setNetwork(network);
    }

    /***
     * copy-constructor
     */
    private Trainer(Optimizer optimizer) {
        this.optimizer = optimizer;
    }

    public Network getNetwork() {
        return optimizer.getNetwork();
    }

    public FitResults fit(@NotNull FitParameters parameters) {
        Matrix x;
        Matrix y;
        List<Matrix> xBatches;
        List<Matrix> yBatches;
        double testLoss;
        double trainLoss;
        double bestTestLoss = Double.MAX_VALUE;

        Network network = getNetwork();
        Network bestNetwork = network;

        FitResults results = new FitResults();
        optimizer.setEpochs(parameters.getEpochs());
        optimizer.calculateDecayLR();

        Map<EarlyStopLossType, Integer> earlyStopTriggeredMap = new HashMap<>();
        for (EarlyStopLossType type: EarlyStopLossType.values())
            earlyStopTriggeredMap.put(type, 0);

        int queryStep = parameters.getEpochs() / parameters.getQueries();
        List<Integer> queryAt = new ArrayList<>();
        for (int i = 0; i < parameters.getQueries(); i++)
            queryAt.add(queryStep * (i + 1));

        MyQueue lastTrainLosses = new MyQueue((int)Math.ceil(parameters.getQueries() * 1.0 / 10));
        MyQueue lastTestLosses = new MyQueue((int)Math.ceil(parameters.getQueries() * 1.0 / 10));

        for (int epoch = 1; epoch <= parameters.getEpochs(); epoch++) {
            x = parameters.getDataset().getTrainData().getInputs().copy();
            y = parameters.getDataset().getTrainData().getOutputs().copy();
            MatrixOperations.shuffleMatrices(x, y);
            xBatches = x.getBatches(parameters.getBatchSize());
            yBatches = y.getBatches(parameters.getBatchSize());
            trainLoss = 0.0;
            for (int batch = 0; batch < xBatches.size(); batch++) {
                trainLoss += network.trainBatch(xBatches.get(batch), yBatches.get(batch)) / parameters.getBatchSize();
                optimizer.step();
            }
            optimizer.decay();
            if (!queryAt.contains(epoch))
                continue;
            x = parameters.getDataset().getTestData().getInputs().copy();
            y = parameters.getDataset().getTestData().getOutputs().copy();
            MatrixOperations.shuffleMatrices(x, y);
            xBatches = x.getBatches(parameters.getBatchSize());
            yBatches = y.getBatches(parameters.getBatchSize());
            testLoss = 0.0;
            for (int batch = 0; batch < xBatches.size(); batch++) {
                testLoss += network.calculateLoss(xBatches.get(batch), yBatches.get(batch)) / parameters.getBatchSize();
            }
            if (testLoss < bestTestLoss) {
                bestTestLoss = testLoss;
                bestNetwork = network.copy();
            }
            results.getTestLossesMap().put(epoch, testLoss);
            lastTrainLosses.push(trainLoss);
            lastTestLosses.push(testLoss);
            logger.info(String.format("Эпоха: %d, потеря при обучении: " + parameters.getDoubleFormat() + ", потеря при тестах: " +
                    parameters.getDoubleFormat(), epoch, trainLoss, testLoss));
            if (!parameters.isEarlyStopping())
                continue;
            if (abortTrain(trainLoss, lastTrainLosses, parameters.getDoubleFormat(), earlyStopTriggeredMap, EarlyStopLossType.TRAIN) ||
                    abortTrain(testLoss, lastTestLosses, parameters.getDoubleFormat(), earlyStopTriggeredMap, EarlyStopLossType.TEST))
                break;
        }
        logger.fine(String.format("В качестве результата обучения сохранена сеть, обеспечившая потерю на тестовой выборке: " +
                parameters.getDoubleFormat(), bestTestLoss));
        results.setBestNetwork(bestNetwork);
        return results;
    }

    private boolean abortTrain(double loss,
                               MyQueue lastLosses,
                               String doubleFormat,
                               Map<EarlyStopLossType, Integer> map,
                               EarlyStopLossType type) {
        String name = null;
        switch (type) {
            case TRAIN:
                name = "обучении";
                break;
            case TEST:
                name = "тестах";
                break;
        }
        if (lastLosses.getActualSize() > 1 && loss >= lastLosses.mean()) {
            logger.warning(String.format("Потеря при %s " + doubleFormat + " превышает последние %d потерь (" + doubleFormat + ")",
                    name, loss, lastLosses.getSize(), lastLosses.mean()));
            map.put(type, map.get(type) + 1);
            if (map.get(type) >= ABORT_THRESHOLD) {
                logger.info(String.format("Ранняя остановка сработала %d раз подряд. Завершение обучения",
                        map.get(type)));
                return true;
            }
        } else
            map.put(type, 0);
        return false;
    }

    @Override
    public Trainer copy() {
        return new Trainer(Utils.copyNullable(optimizer));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trainer)) return false;
        Trainer trainer = (Trainer) o;
        return Objects.equals(optimizer, trainer.optimizer);
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

    private String getClassName() {
        return "Тренер";
    }

    private String getDebugClassName() {
        return "Trainer";
    }

    enum EarlyStopLossType {
        TRAIN,
        TEST
    }
}