package models.trainers;

import models.data.Data;
import models.data.Dataset;
import models.networks.Network;
import models.optimizers.Optimizer;
import utils.Errors;

import java.util.*;
import java.util.logging.Logger;

/**
 * Тренер, запускающий обучение сети.
 */
public class Trainer {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /**
     * Запуск обучения по заданным параметрам
     * @param parameters параметры обучения
     * @return           результаты обучения
     */
    public static FitResults fit(FitParameters parameters) {
        if (parameters.isPreTrainRequired())
            return fitWithPreTrain(parameters);
        return fitSingleTry(parameters, parameters.getNetworkBuilder().build());
    }

    /**
     * Запуск обучения заданной сети. Основной метод, реализующий обучение
     * @param parameters параметры обучения
     * @param network    сеть
     * @return           результаты корректировки
     */
    private static FitResults fitSingleTry(FitParameters parameters, Network network) {
        long startTime = System.currentTimeMillis();  // фиксирование момента запуска метода
        double bestTestLoss = Double.MAX_VALUE;  // наилучшая потеря на тестовой выборке
        Network bestNetwork = network;  // сеть, обеспечившая наилучшую потерю

        Dataset dataset = parameters.getDataset();  // получение обучающей выборки

        Map<Integer, Double> testLossesMap = new HashMap<>();  // мапа зависимости потери от эпохи

        // настройка оптимизатора в соответствии с полученными параметрами обучения
        Optimizer optimizer = parameters.getOptimizerBuilder()
                .network(network)
                .epochs(parameters.getEpochs())
                .build();

        // мапа для проверки ранней остановки
        Map<EarlyStopLossType, Integer> earlyStopTriggeredMap = new HashMap<>();
        for (EarlyStopLossType type: EarlyStopLossType.values())
            earlyStopTriggeredMap.put(type, 0);

        MyQueue lastTrainLosses = new MyQueue((int)Math.ceil(parameters.getQueries() * 1.0 / 10));
        MyQueue lastTestLosses = new MyQueue((int)Math.ceil(parameters.getQueries() * 1.0 / 10));

        lastTrainLosses.setMax();
        lastTestLosses.setMax();

        // вычисление номеров эпох, когда нужно выполнять опросы
        List<Integer> queryAt = calcQueryAt(parameters.getEpochs(), parameters.getQueries(), parameters.getQueriesRangeType());

        for (int epoch = 1; epoch <= parameters.getEpochs(); epoch++) {
            double trainLoss = 0.0;  // потеря на обучающей выборке

            // разбиение обучающей выборки на пакеты с перемешиванием и опциональным зашумлением
            Iterable<Data> trainBatches = dataset.getTrainData().getBatchesGenerator(
                    parameters.getBatchSize(),
                    true, parameters.isRenoiseData());

            for (Data batch: trainBatches) {  // перебор пакетов
                trainLoss += network.trainBatch(batch.getInputs(), batch.getOutputs());  // обучение по пакету
                optimizer.step();  // корректировка параметров
            }

            optimizer.decay();  // снижение скорости обучения
            if (!queryAt.contains(epoch))  // нужна ли оценка
                continue;  // если не нужна, то запускается очередная эпоха обучения

            double testLoss = 0.0;  // потеря на тестовой выборке

            // разбиение тестовой выборки на пакеты без перемешивания и зашумления
            Iterable<Data> testBatches = dataset.getTestData().getBatchesGenerator(
                    parameters.getBatchSize(),
                    false, false);

            for (Data batch: testBatches)  // вычисление потери на тестовой выборке
                testLoss += network.calculateLoss(batch.getInputs(), batch.getOutputs());

            if (testLoss < bestTestLoss) {  // сохранение наилучших результатов
                bestTestLoss = testLoss;
                bestNetwork = network.deepCopy();
            }

            // сохранение значений потерь
            testLossesMap.put(epoch, testLoss);
            lastTrainLosses.push(trainLoss);
            lastTestLosses.push(testLoss);

            logger.info(String.format("Эпоха: %d, потеря при обучении: " + parameters.getDoubleFormat() + ", потеря при тестах: " +
                    parameters.getDoubleFormat(), epoch, trainLoss, testLoss));

            if (!parameters.isEarlyStopping())  // нужна ли ранняя остановка
                continue;  // если не нужна, то запускается очередная эпоха обучения

            if (abortTrain(trainLoss, lastTrainLosses, parameters.getDoubleFormat(), earlyStopTriggeredMap,
                    EarlyStopLossType.TRAIN, parameters.getEarlyStoppingThreshold()) ||
                abortTrain(testLoss, lastTestLosses, parameters.getDoubleFormat(), earlyStopTriggeredMap,
                    EarlyStopLossType.TEST, parameters.getEarlyStoppingThreshold()))
                break;  // если сработало условие ранней остановки, то цикл завершается
        }
        logger.fine(String.format("В качестве результата обучения сохранена сеть, обеспечившая потерю на тестовой выборке: " +
                parameters.getDoubleFormat(), bestTestLoss));

        // формирование результатов обучения
        return new FitResults(testLossesMap,                                   // зависимость потери от эпохи
                bestNetwork,                                                   // обученная сеть
                new Errors(                                                    // ошибки
                    dataset.getValidData().getOutputs(),                       // целевые значения
                    bestNetwork.forward(dataset.getValidData().getInputs())),  // вычисленные значения
                dataset,                                                       // обучающая выборка
                startTime,                                                     // время начала обучения
                System.currentTimeMillis());                                   // время окончания обучения
    }

    /**
     * Обучение сети с предобучением. Механизм предобучения:
     * <pre><ul>
     * <li>FitParameters.preTrainsCount раз запускается попытка обучить сеть с уменьшенным в
     * FitParameters.preTrainReduceFactor раз количеством эпох.</li>
     * <li>По каждой попытке обучения фиксируется потеря.</li>
     * <li>Из всех попыток для дальнейшего обучения со всеми эпохами выбирается та сеть, которая
     * обеспечила наименьшую потерю.</li>
     * </ul></pre>
     * @param parameters параметры обучения
     * @return           результаты обучения
     */
    private static FitResults fitWithPreTrain(FitParameters parameters) {
        Network bestNetwork = null;
        double bestLoss = Double.MAX_VALUE;
        Dataset dataset = parameters.getDataset();
        FitParameters preTrainParameters = parameters.preTrainCopy();
        for (int preTrain = 0; preTrain < parameters.getPreTrainsCount(); preTrain++) {
            Network untrainedNetwork = parameters.getNetworkBuilder().build();
            FitResults results = fitSingleTry(preTrainParameters.deepCopy(),
                    untrainedNetwork.deepCopy());
            Network trainedNetwork = results.getNetwork();
            double loss = trainedNetwork.calculateLoss(dataset.getValidData().getInputs(),
                    dataset.getValidData().getOutputs());
            if (loss < bestLoss) {
                bestLoss = loss;
                bestNetwork = untrainedNetwork;
            }
        }
        return fitSingleTry(parameters, bestNetwork);
    }

    /**
     * Ранняя остановка при обучении
     * @param loss           текущая потеря
     * @param lastLosses     несколько последних потерь
     * @param doubleFormat   формат вывода двоичных чисел
     * @param map            потери
     * @param type           тип потери
     * @param abortThreshold сколько раз подряд текущая потеря должна быть не лучше, чем предыдущие потери
     * @return               true, если обучение необходимо остановить
     */
    private static boolean abortTrain(double loss,
                                      MyQueue lastLosses,
                                      String doubleFormat,
                                      Map<EarlyStopLossType, Integer> map,
                                      EarlyStopLossType type,
                                      int abortThreshold) {
        String name = null;
        switch (type) {
            case TRAIN:
                name = "обучении";
                break;
            case TEST:
                name = "тестах";
                break;
        }
        if (lastLosses.getActualSize() > 1 && loss >= lastLosses.mean()) {  // если текущая потеря хуже предыдущих
            logger.warning(String.format("Потеря при %s " + doubleFormat + " превышает последние %d потерь (" + doubleFormat + ")",
                    name, loss, lastLosses.getSize(), lastLosses.mean()));
            map.put(type, map.get(type) + 1);  // увеличение счетчика сработавших проверок
            if (map.get(type) >= abortThreshold) {  // если достигнут порог
                logger.info(String.format("Ранняя остановка сработала %d раз подряд. Завершение обучения",
                        map.get(type)));
                return true;  // то обучение надо прекратить
            }
        } else  // если потеря не хуже предыдущих
            map.put(type, 0);  // то счетчик сработавших проверок сбрасывается
        return false;  // обучение прекращать не надо
    }

    /**
     * Вычисление эпох, когда надо выполнять опросы
     * @param epochs  количество эпох
     * @param queries количество опросов
     * @param type    стратегия опросов
     * @return        номера эпох, когда надо выполнять опросы
     */
    private static List<Integer> calcQueryAt(int epochs, int queries, QueriesRangeType type) {
        if (queries == 1)  // если опрос 1, то выполняется в конце
            return Collections.singletonList(epochs);
        if (queries == 2)  // если опроса 2, то они выполняются в начале и в конце
            return Arrays.asList(1, epochs);

        List<Integer> queryAt = new ArrayList<>();
        queryAt.add(1);  // первый опрос всегда выполняется в начале обучения
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
        queryAt.add(epochs);  // последний опрос всегда выполняется в конце обучения
        return queryAt;
    }

    /**
     * Тип потери, по которой осуществляется ранняя остановка
     */
    enum EarlyStopLossType {
        /**
         * Потеря при обучении
         */
        TRAIN,

        /**
         * Потеря на тестовой выборке
         */
        TEST
    }
}