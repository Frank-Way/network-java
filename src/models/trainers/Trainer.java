package models.trainers;

import com.sun.istack.internal.NotNull;
import models.math.Matrix;
import models.math.MatrixOperations;
import models.networks.Network;
import models.networks.NetworkBuilder;
import models.networks.NetworkBuilderParameters;
import models.optimizers.Optimizer;
import utils.Errors;

import java.util.*;
import java.util.logging.Logger;

/**
 * Тренер, запускающий обучение сети. Атрибуты модели:
 *  {@link Logger} - логгер;
 *  ABORT_THRESHOLD - порог, сколько раз подряд потеря должна не уменьшиться по мере обучения (используется, если
 *                    earlyStopping в {@link FitParameters} true).
 */
public class Trainer {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final int ABORT_THRESHOLD = 3;

    /**
     * Запуск обучения заданной сети
     * @param parameters  параметры обучения
     * @param network  сеть
     * @return  результаты корректировки
     */
    private static FitResults fitSingleTry(FitParameters parameters, Network network) {
        Matrix x;
        Matrix y;
        List<Matrix> xBatches;
        List<Matrix> yBatches;

        double bestTestLoss = Double.MAX_VALUE;  // наилучшая потеря на тестовой выборке
        Network bestNetwork = network;  // сеть, обеспечившая наилучшую потерю

        Map<Integer, Double> testLossesMap = new HashMap<>();  // мапа зависимости потери от эпохи

        // настройка оптимизатора в соответствии с полученными параметрами обучения
        Optimizer optimizer = parameters.getOptimizer();
        optimizer.setNetwork(network);
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

        lastTrainLosses.setMax();
        lastTestLosses.setMax();

        for (int epoch = 1; epoch <= parameters.getEpochs(); epoch++) {
            x = parameters.getDataset().getTrainData().getInputs().copy();
            y = parameters.getDataset().getTrainData().getOutputs().copy();
            MatrixOperations.shuffleMatrices(x, y);
            xBatches = x.getBatches(parameters.getBatchSize());
            yBatches = y.getBatches(parameters.getBatchSize());
            double trainLoss = 0.0;
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
            double testLoss = 0.0;
            for (int batch = 0; batch < xBatches.size(); batch++) {
                testLoss += network.calculateLoss(xBatches.get(batch), yBatches.get(batch)) / parameters.getBatchSize();
            }
            if (testLoss < bestTestLoss) {
                bestTestLoss = testLoss;
                bestNetwork = network.copy();
            }
            testLossesMap.put(epoch, testLoss);
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

        return new FitResults(testLossesMap,
                bestNetwork,
                Errors.buildFromTargetsAndPredictions(parameters.getDataset().getValidData().getOutputs(),
                        bestNetwork.forward(parameters.getDataset().getValidData().getInputs())),
                parameters.getDataset());
    }

    private static FitResults fitWithPreTrain(FitParameters parameters) {
        Network bestNetwork = null;
        double bestLoss = Double.MAX_VALUE;
        FitParameters preTrainParameters = parameters.preTrainCopy();
        for (int preTrain = 0; preTrain < parameters.getPreTrainsCount(); preTrain++) {
            FitResults results = fitSingleTry(preTrainParameters.copy(),
                    NetworkBuilder.build(parameters.getNetworkBuilderParameters()));
            Network network = results.getNetwork();
            double loss = network.calculateLoss(parameters.getDataset().getValidData().getInputs(),
                    parameters.getDataset().getValidData().getOutputs());
            if (loss < bestLoss) {
                bestLoss = loss;
                bestNetwork = network;
            }
        }
        return fitSingleTry(parameters, bestNetwork);
    }

    public static FitResults fit(@NotNull FitParameters parameters) {
        if (parameters.isPreTrainRequired())
            return fitWithPreTrain(parameters);
        return fitSingleTry(parameters, NetworkBuilder.build(parameters.getNetworkBuilderParameters()));
    }

    private static boolean abortTrain(double loss,
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

    enum EarlyStopLossType {
        TRAIN,
        TEST
    }
}