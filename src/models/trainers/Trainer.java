package models.trainers;

import models.data.Data;
import models.data.Dataset;
import models.networks.Network;
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
    private static final int ABORT_THRESHOLD = 2;

    /**
     * Запуск обучения по заданным параметрам
     * @param parameters параметры обучения
     * @return результаты обучения
     */
    public static FitResults fit(FitParameters parameters) {
        if (parameters.isPreTrainRequired())
            return fitWithPreTrain(parameters);
        return fitSingleTry(parameters, parameters.getNetworkBuilder().build());
    }

    /**
     * Запуск обучения заданной сети
     * @param parameters  параметры обучения
     * @param network  сеть
     * @return  результаты корректировки
     */
    private static FitResults fitSingleTry(FitParameters parameters, Network network) {
        long startTime = System.currentTimeMillis();
        double bestTestLoss = Double.MAX_VALUE;  // наилучшая потеря на тестовой выборке
        Network bestNetwork = network;  // сеть, обеспечившая наилучшую потерю

        Dataset dataset = parameters.getDataset();

        Map<Integer, Double> testLossesMap = new HashMap<>();  // мапа зависимости потери от эпохи

        // настройка оптимизатора в соответствии с полученными параметрами обучения
        Optimizer optimizer = parameters.getOptimizerBuilder()
                .network(network)
                .epochs(parameters.getEpochs())
                .build();

        Map<EarlyStopLossType, Integer> earlyStopTriggeredMap = new HashMap<>();
        for (EarlyStopLossType type: EarlyStopLossType.values())
            earlyStopTriggeredMap.put(type, 0);

        List<Integer> queryAt = calcQueryAt(parameters.getEpochs(), parameters.getQueries(), parameters.getQueriesRangeType());

        MyQueue lastTrainLosses = new MyQueue((int)Math.ceil(parameters.getQueries() * 1.0 / 10));
        MyQueue lastTestLosses = new MyQueue((int)Math.ceil(parameters.getQueries() * 1.0 / 10));

        lastTrainLosses.setMax();
        lastTestLosses.setMax();

        for (int epoch = 1; epoch <= parameters.getEpochs(); epoch++) {
            double trainLoss = 0.0;  // потеря на обучающей выборке
            for (Data batch: dataset.getTrainData().getBatchesGenerator(parameters.getBatchSize(),
                    true)) {
                // обучение
                trainLoss += network.trainBatch(batch.getInputs(), batch.getOutputs());
                optimizer.step();  // корректировка параметров
            }

            optimizer.decay();  // снижение скорости обучения
            if (!queryAt.contains(epoch))  // нужна ли оценка
                continue;

            double testLoss = 0.0;  // потеря на тестовой выборке
            for (Data batch: dataset.getTestData().getBatchesGenerator(parameters.getBatchSize(),
                    true))
                testLoss += network.calculateLoss(batch.getInputs(), batch.getOutputs());

            if (testLoss < bestTestLoss) {  // сохранение наилучших результатов
                bestTestLoss = testLoss;
                bestNetwork = network.deepCopy();
            }

            testLossesMap.put(epoch, testLoss);
            lastTrainLosses.push(trainLoss);
            lastTestLosses.push(testLoss);

            logger.info(String.format("Эпоха: %d, потеря при обучении: " + parameters.getDoubleFormat() + ", потеря при тестах: " +
                    parameters.getDoubleFormat(), epoch, trainLoss, testLoss));

            if (!parameters.isEarlyStopping())  // нужна ли ранняя остановка
                continue;
            if (abortTrain(trainLoss, lastTrainLosses, parameters.getDoubleFormat(), earlyStopTriggeredMap, EarlyStopLossType.TRAIN) ||
                    abortTrain(testLoss, lastTestLosses, parameters.getDoubleFormat(), earlyStopTriggeredMap, EarlyStopLossType.TEST))
                break;
        }
        logger.fine(String.format("В качестве результата обучения сохранена сеть, обеспечившая потерю на тестовой выборке: " +
                parameters.getDoubleFormat(), bestTestLoss));

        return new FitResults(testLossesMap,
                bestNetwork,
                Errors.buildFromTargetsAndPredictions(dataset.getValidData().getOutputs(),
                        bestNetwork.forward(dataset.getValidData().getInputs())),
                dataset,
                System.currentTimeMillis() - startTime);
    }

    private static FitResults fitWithPreTrain(FitParameters parameters) {
        Network bestNetwork = null;
        double bestLoss = Double.MAX_VALUE;
        Dataset dataset = parameters.getDataLoader().load(parameters.getLoadParameters());
        FitParameters preTrainParameters = parameters.preTrainCopy();
        for (int preTrain = 0; preTrain < parameters.getPreTrainsCount(); preTrain++) {
            FitResults results = fitSingleTry(preTrainParameters.deepCopy(),
                    parameters.getNetworkBuilder().build());
            Network network = results.getNetwork();
            double loss = network.calculateLoss(dataset.getValidData().getInputs(),
                    dataset.getValidData().getOutputs());
            if (loss < bestLoss) {
                bestLoss = loss;
                bestNetwork = network;
            }
        }
        return fitSingleTry(parameters, bestNetwork);
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

    private static List<Integer> calcQueryAt(int epochs, int queries, QueriesRangeType type) {
        if (queries == 1)
            return Collections.singletonList(epochs);
        if (queries == 2)
            return Arrays.asList(1, epochs);
        List<Integer> queryAt = new ArrayList<>();
        queryAt.add(1);
        switch (type) {
            case LINEAR:
                int queryStep = epochs / queries;
                for (int i = 1; i < queries - 1; i++)
                    queryAt.add(queryStep * i);
                break;
            case NON_LINEAR:
                int firstQuery = (int) Math.ceil(epochs * 1.0 / 100);
                double factor = Math.pow(epochs * 1.0 / firstQuery, 1.0 / (queries - 2));
                int query = firstQuery;
                do {
                    queryAt.add(query);
                    query = (int) Math.ceil(query * factor);
                } while (queryAt.size() < queries - 1 && query < epochs);
                break;
        }
        queryAt.add(epochs);
        return queryAt;
    }

    enum EarlyStopLossType {
        TRAIN,
        TEST
    }
}