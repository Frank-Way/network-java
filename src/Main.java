import models.trainers.FitResults;
import options.AppProperties;
import options.PrintOptions;
import serialization.SerializationType;
import serialization.SerializationUtils;
import serialization.exceptions.SerializationException;
import utils.MyTask;
import utils.Utils;
import utils.automatization.Experiment;
import utils.automatization.ExperimentBuilder;
import utils.automatization.RunConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Основная программа для обучения сетей в соответствии с экспериментами, представленных {@link Experiment}
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        final long startTime = System.currentTimeMillis();  // фиксирование времени запуска программы

        // чтение настроек логгирования
        try {
            LogManager logManager = LogManager.getLogManager();
            Class<Main> aClass = Main.class;
            InputStream inputStream = aClass.getResourceAsStream("logging.properties");
            logManager.readConfiguration(inputStream);
            logger.fine("Успешно считаны конфигурации для логгеров");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // чтение настроек приложения
        final AppProperties appProperties;
        try {
            appProperties = new AppProperties();
            logger.fine("Успешно считаны настройки приложения");
            logger.finer(appProperties.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // чтение описания экспериментов
        final ExperimentBuilder[] experimentBuilders;
        switch (appProperties.getExperimentsSourceType()) {
            case CODE:  // получение из кода
                experimentBuilders = Experiments.getDefaultExperimentBuilders(appProperties.getDoubleFormat());
                break;
            case YAML_FILE:  // получение из файла
                try {
                    experimentBuilders = Experiments.getExperimentBuildersFromFile(
                            appProperties.getExperimentsSourceYamlPath(),
                            appProperties.getExperimentsSourceYamlFilename(),
                            SerializationType.YAML);
                } catch (serialization.exceptions.SerializationException e) {
                    e.printStackTrace();
                    return;
                }
                break;
            default:
                throw new IllegalArgumentException("Не известный тип источника описания экспериментов: " +
                        appProperties.getExperimentsSourceType());
        }

        logger.fine("Успешно считаны конфигурации экспериментов");
        logger.finer(Arrays.toString(experimentBuilders));

        // формирование всех обучающих выборок до клонирования и распихивания по потокам
        Experiment[] experiments = Arrays.stream(experimentBuilders).map(ExperimentBuilder::build).toArray(Experiment[]::new);

        logger.fine("Успешно загружены все обучающие выборки");

        logger.fine("Начало запуска экспериментов");
        // создание сервиса с фиксированным пулом тредов
        ExecutorService executorService = Executors.newFixedThreadPool(appProperties.getThreadPoolSize());

        // маппинг экспериментов на соответствующие им конфигурации
        Map<Experiment, Set<RunConfiguration>> experimentToConfigMap = new HashMap<>();
        Arrays.stream(experiments).forEach(experiment ->
                experimentToConfigMap.put(experiment,
                        Arrays.stream(experiment.getRunConfigurations()).collect(Collectors.toSet())));

        // маппинг конфигураций на соответствующие им эксперимента
        Map<RunConfiguration, Experiment> configToExperimentMap = new HashMap<>();
        Arrays.stream(experiments).forEach(experiment ->
                Arrays.stream(experiment.getRunConfigurations()).forEach(runConfiguration ->
                        configToExperimentMap.put(runConfiguration, experiment)));

        // асинхронный запуск обучений и маппинг конфигураций на набор результатов
        // каждая конфигурация запускается заданное количество раз
        Map<RunConfiguration, Set<Future<FitResults>>> runConfigurationToFuturesToResultsMap = new HashMap<>();
        for (Experiment experiment : experiments)
            for (RunConfiguration runConfiguration: experiment.getRunConfigurations())
                runConfigurationToFuturesToResultsMap.put(runConfiguration,
                        IntStream.range(0, runConfiguration.getRetries())
                                .mapToObj(value -> executorService.submit(new MyTask(runConfiguration.getFitParameters().deepCopy())))
                                .collect(Collectors.toSet()));

        // сборка всех результатов в одно место
        Set<Future<FitResults>> allFutures = runConfigurationToFuturesToResultsMap.values().stream()
                .flatMap(Set::stream).collect(Collectors.toSet());

        // обработанные результаты обучения
        Set<Future<FitResults>> processedFutures = new HashSet<>();

        // результаты обучения обрабатываются синхронно в главном потоке
        // в цикле периодически проверяются новые необработанные результаты обучения
        while (!allFuturesDone(allFutures)) {  // пока не завершены все запущенные попытки обучения
            for (Future<FitResults> future: allFutures.stream()  // для каждого результата
                    .filter(f -> f.isDone() && !processedFutures.contains(f))  // завершенного, но не обработанного
                    .collect(Collectors.toSet())) {  // если новых результатов нет, то цикл for не запустится
                // берётся конфигурации запуска и эксперимента
                RunConfiguration runConfiguration = reverseSearch(runConfigurationToFuturesToResultsMap, future);
                Experiment experiment = configToExperimentMap.get(runConfiguration);
                // берутся полученные результаты
                FitResults fitResults = getFromFuture(future);

                processedFutures.add(future);  // запоминается факт обработки результата

                // обработка результатов для каждого запуска
                tryToPrint(appProperties.getPrintConfigurationEach(), runConfiguration, fitResults,
                        String.format("Результаты обучения для конфигурации запуска [%s] эксперимента [%s] \n",
                                runConfiguration.getDescription(), experiment.getDescription()),
                        appProperties.isPrintRequired(), appProperties.isDebugMode(), appProperties.getDoubleFormat());

                tryToSave(appProperties.isSaveRequired() && appProperties.isSaveConfigurationEach(),
                        runConfiguration, fitResults, appProperties.getSaveFilenamePattern(),
                        appProperties.getSavePath(), appProperties.getSaveSerializationType(),
                        appProperties.getDoubleFormat());

                // если обрабатываемый результат был последним для соответствующей конфигурации запуска,
                // то есть получены все результаты для соответствующей конфигурации
                if (allFuturesDone(runConfigurationToFuturesToResultsMap.get(runConfiguration))) {
                    // тогда начинается обработка результатов для данной конфигурации
                    Future<FitResults> bestFuture = findBestFuture(runConfigurationToFuturesToResultsMap.get(runConfiguration));
                    FitResults bestFitResults = getFromFuture(bestFuture);

                    tryToPrint(appProperties.getPrintConfigurationBest(), runConfiguration, bestFitResults,
                            String.format("Наилучшие результаты обучения для конфигурации запуска [%s] эксперимента [%s] \n",
                                    runConfiguration.getDescription(), experiment.getDescription()),
                            appProperties.isPrintRequired(), appProperties.isDebugMode(), appProperties.getDoubleFormat());

                    tryToSave(appProperties.isSaveRequired() && appProperties.isSaveConfigurationBest(),
                            runConfiguration, bestFitResults, appProperties.getSaveFilenamePattern(),
                            appProperties.getSavePath(), appProperties.getSaveSerializationType(),
                            appProperties.getDoubleFormat());

                    logTimeSpent(runConfigurationToFuturesToResultsMap.get(runConfiguration),
                            String.format("Запуск конфигурации [%s] эксперимента [%s] занял",
                                    runConfiguration.getDescription(), experiment.getDescription()));
                }

                // формирование результатов для эксперимента, соответствующего той конфигурации,
                // по которой пришли обрабатываемые результаты
                Set<Future<FitResults>> thisExperimentFutures = runConfigurationToFuturesToResultsMap.entrySet().stream()
                        .filter(entry -> experimentToConfigMap.get(experiment).contains(entry.getKey()))
                        .map(Map.Entry::getValue)
                        .flatMap(Set::stream)
                        .collect(Collectors.toSet());

                if (allFuturesDone(thisExperimentFutures)) {
                    // аналогичная проверка перед обработкой результатов для эксперимента, соответствующему
                    // полученному результату
                    Future<FitResults> bestFuture = findBestFuture(thisExperimentFutures);
                    RunConfiguration bestRunConfiguration = reverseSearch(runConfigurationToFuturesToResultsMap, bestFuture);
                    FitResults bestFitResults = getFromFuture(bestFuture);

                    tryToPrint(appProperties.getPrintExperimentBest(), bestRunConfiguration, bestFitResults,
                            String.format("Наилучшие результаты обучения для всех конфигураций запуска эксперимента [%s]" +
                                            " (соответствуют конфигурации [%s]) \n",
                                    experiment.getDescription(), bestRunConfiguration.getDescription()),
                            appProperties.isPrintRequired(), appProperties.isDebugMode(), appProperties.getDoubleFormat());

                    tryToSave(appProperties.isSaveRequired() && appProperties.isSaveExperimentBest(),
                            bestRunConfiguration, bestFitResults, appProperties.getSaveFilenamePattern(),
                            appProperties.getSavePath(), appProperties.getSaveSerializationType(),
                            appProperties.getDoubleFormat());

                    logTimeSpent(thisExperimentFutures,
                            String.format("Выполнение эксперимента [%s] заняло",
                                    experiment.getDescription()));
                }
            }  // завершение обработки полученных результатов
            Utils.myWait(1_000);  // таймаут перед проверкой наличия новых результатов
            logger.fine(String.format("Количество активных потоков: %d. Завершено: %d/%d.",
                    ((ThreadPoolExecutor) executorService).getActiveCount(),
                    processedFutures.size(), allFutures.size()));
        }

        // после получения всех результатов обязательно завершается executorService, иначе программа никогда не
        // завершится, то есть станет демоном
        executorService.shutdown();

        // обработка результатов для всех экспериментов
        Future<FitResults> bestFuture = findBestFuture(allFutures);
        RunConfiguration bestRunConfiguration = reverseSearch(runConfigurationToFuturesToResultsMap, bestFuture);
        Experiment bestExperiment = configToExperimentMap.get(bestRunConfiguration);
        FitResults bestFitResults = getFromFuture(bestFuture);

        tryToPrint(appProperties.getPrintExperimentBest(), bestRunConfiguration, bestFitResults,
                String.format("Наилучшие результаты обучения для всех конфигураций запуска всех экспериментов " +
                                " (соответствуют конфигурации [%s] эксперимента [%s]) \n",
                        bestRunConfiguration.getDescription(), bestExperiment.getDescription()),
                appProperties.isPrintRequired(), appProperties.isDebugMode(), appProperties.getDoubleFormat());

        tryToSave(appProperties.isSaveRequired() && appProperties.isSaveExperimentBest(),
                bestRunConfiguration, bestFitResults, appProperties.getSaveFilenamePattern(),
                appProperties.getSavePath(), appProperties.getSaveSerializationType(),
                appProperties.getDoubleFormat());

        logTimeSpent(allFutures, "Выполнение всех экспериментов заняло");

        logger.fine("Завершение программы. Программа работала - " +
                Utils.millisToHMS(System.currentTimeMillis() - startTime));
    }

    /**
     * Проверка, сформированы ли результаты для всех Future
     * @param futures  набор Future
     * @param <T>  тип результата
     * @return  true, если для всех Future готовы результаты
     */
    private static <T> boolean allFuturesDone(Set<Future<T>> futures) {
        return futures.stream()
                // из каждого Future берётся факт готовности результатов
                .map(Future::isDone)
                // факты фильтруются так, что остаются только false
                .filter(aBoolean -> !aBoolean)
                // ищется хоть один false
                // если он есть, то false и вернётся как результат метода
                .findAny()
                // иначе вернётся true (т.к. нет ни одного false)
                .orElse(true);
    }

    /**
     * Поиск ключа по одному из значений в мапе
     * @param map  мапа для поиска, значением которой является набор элементов
     * @param value  элемент набора, который является одним из значений мапы
     * @param <K>  тип ключа мапы
     * @param <V>  тип элемента набора значения мапы
     * @return ключ мапы
     */
    private static <K, V> K reverseSearch(Map<K, Set<V>> map, V value) {
        return map.entrySet().stream()
                // мапа фильтруется так, что остаются элементы, значения которых содержат нужное значение
                .filter(entry -> entry.getValue().contains(value))
                // ищется хоть один элемент мапы
                .findAny()
                // если элемента нет, выбрасывается соответствующее исключение
                .orElseThrow(() -> new IllegalArgumentException("Не найден ключ по значению: " + value))
                // из элемента мапы берётся ключ, который и будет результатом метода
                .getKey();
    }

    /**
     * Вывод результатов в лог (при необходимости)
     * @param printOptions  настройки вывода
     * @param runConfiguration  конфигурация
     * @param fitResults  результаты, соответствующие конфигурации
     * @param prompt  префикс, описывающий данный вывод (к чему он относится)
     * @param printRequired  нужен ли этот вывод вообще
     * @param debugMode  включен ли debug-режим
     * @param doubleFormat  формат вывода вещественных чисел
     */
    private static void tryToPrint(PrintOptions printOptions, RunConfiguration runConfiguration, FitResults fitResults,
                                   String prompt, boolean printRequired, boolean debugMode, String doubleFormat) {
        if (printRequired && printOptions.isRequired()) {
            logger.info(prompt + Utils.runConfigurationAndFitResultsToString(
                            runConfiguration, fitResults, printOptions,
                            debugMode, doubleFormat));
        }
    }

    /**
     * Сохранение сети в файл
     * @param saveRequired  нужно ли сохранять
     * @param runConfiguration  конфигурация
     * @param fitResults  результаты обучения
     * @param saveFilenamePattern  паттерн для формирования имени файла
     * @param saveFolder  папка для сохранения
     * @param serializationType  тип сериализации
     * @param doubleFormat  формат вывода вещественных чисел
     */
    private static void tryToSave(boolean saveRequired, RunConfiguration runConfiguration, FitResults fitResults,
                                  String saveFilenamePattern, String saveFolder, SerializationType serializationType,
                                  String doubleFormat) {
        if (saveRequired) {
            // при формировании имени файла используется случайный уникальный идентификатор
            String filename = String.format(saveFilenamePattern,
                    UUID.randomUUID().toString().substring(0, 5));
            logger.info("Сохранение нейросети в файл: " + filename);
            try {
                SerializationUtils.save(fitResults.getNetwork(), saveFolder, filename,
                        serializationType, doubleFormat);
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    /**
     * Получение результатов из Future, исключения логируются и оборачиваются в RuntimeException
     * @param future  Future
     * @param <T>  тип результата Future
     * @return  результат
     */
    private static <T> T getFromFuture(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.severe("Ошибка при обработке результатов: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Вывод таймингов в лог по набору конфигураций
     * @param futures  набор результатов обучения
     * @param prompt  префикс, описывающий данный вывод (к чему он относится)
     */
    private static void logTimeSpent(Set<Future<FitResults>> futures, String prompt) {
        // суммарное время обучения
        long totalTimeSpent = futures.stream().map(Main::getFromFuture)
                .mapToLong(result -> result.getTimeStop() - result.getTimeStart())
                .sum();

        logger.fine(String.format("%s суммарно - %s", prompt, Utils.millisToHMS(totalTimeSpent)));

        logger.fine(String.format("%s в среднем - %s",
                prompt, Utils.millisToHMS(totalTimeSpent / futures.size())));

        // время запуска обучения данного набора конфигураций (т.е. время запуска первой
        // конфигурации из набора)
        long minStartTime = futures.stream().map(Main::getFromFuture)
                // ищется минимальное время запуска
                .map(FitResults::getTimeStart).min(Long::compare)
                // иначе возвращается некорректный результат
                .orElse(Long.MAX_VALUE);

        // время завершения обучения данного набора конфигураций (т.е. время окончания
        // обучения последней конфигурации из набора)
        long maxStopTime = futures.stream().map(Main::getFromFuture)
                // ищется максимальное время завершения
                .map(FitResults::getTimeStop).max(Long::compare)
                // иначе возвращается некорректный результат
                .orElse(Long.MIN_VALUE);

        logger.fine(String.format("%s фактически - %s",
                prompt, Utils.millisToHMS(maxStopTime - minStartTime)));
    }

    /**
     * Поиск наилучшего результата (наилучший результат тот, у которого наименьшая абсолютная ошибка)
     * @param futures  набор результатов
     * @return  наилучший результат
     */
    private static Future<FitResults> findBestFuture(Set<Future<FitResults>> futures) {
        return futures.stream()
                .min(Comparator.comparingDouble(future -> getFromFuture(future).getMaxAbsoluteError()))
                .orElseThrow(() -> new RuntimeException("Ошибка при обработке результатов"));
    }

}

