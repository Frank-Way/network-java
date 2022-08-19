import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import models.trainers.FitResults;
import options.AppProperties;
import options.PrintOptions;
import serialization.SerializationType;
import serialization.SerializationUtils;
import utils.ExperimentConfiguration;
import utils.MyTask;
import utils.RunConfiguration;
import utils.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Основная программа для обучения сетей в соответствии с экспериментами, описанными в {@link ExperimentConfigurations}
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
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
        final AppProperties appProperties;
        try {
            appProperties = new AppProperties();
            logger.fine("Успешно считаны настройки приложения");
            logger.finer(appProperties.toString());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        List<ExperimentConfiguration> experimentConfigurations = ExperimentConfigurations.getTwoDefaultExperimentConfigurations();
//        List<ExperimentConfiguration> experimentConfigurations = ExperimentConfigurations.getExperimentConfigurations();

        logger.fine("Успешно считаны конфигурации экспериментов");
        logger.finer(experimentConfigurations.stream()
                .map(ec -> ec.toString(appProperties.isDebugMode()))
                .collect(Collectors.toList()).toString());

        logger.fine("Начало запуска экспериментов");
        ExecutorService executorService = Executors.newFixedThreadPool(appProperties.getThreadPoolSize());

        Map<RunConfiguration, Set<Future<FitResults>>> runConfigurationToFuturesToResultsMap = new HashMap<>();
        for (ExperimentConfiguration experimentConfiguration: experimentConfigurations)
            for (RunConfiguration runConfiguration: experimentConfiguration.getRunConfigurations())
                runConfigurationToFuturesToResultsMap.put(runConfiguration,
                        IntStream.range(0, runConfiguration.getRetries())
                                .mapToObj(value -> executorService.submit(new MyTask(runConfiguration.getFitParameters().copy())))
                                .collect(Collectors.toSet()));

        Map<ExperimentConfiguration, Set<Future<FitResults>>> experimentConfigurationToFuturesMap = new HashMap<>();
        for (ExperimentConfiguration experimentConfiguration: experimentConfigurations) {
            experimentConfigurationToFuturesMap.put(experimentConfiguration, new HashSet<>());
            for (Map.Entry<RunConfiguration, Set<Future<FitResults>>> entry : runConfigurationToFuturesToResultsMap.entrySet())
                if (experimentConfiguration.getRunConfigurations().contains(entry.getKey()))
                    experimentConfigurationToFuturesMap.get(experimentConfiguration).addAll(entry.getValue());
        }

        Set<Future<FitResults>> allFutures = runConfigurationToFuturesToResultsMap.values().stream()
                .flatMap(Set::stream).collect(Collectors.toSet());

        Set<Future<FitResults>> processedFutures = new HashSet<>();

        while (!allFuturesDone(allFutures)) {  // пока не завершены все запущенные попытки обучения
            for (Future<FitResults> future: allFutures.stream()  // для каждого результата
                    .filter(f -> f.isDone() && !processedFutures.contains(f))  // завершенного, но не обработанного
                    .collect(Collectors.toSet())) {  // если новых результатов нет, то цикл for не запустится
                // берётся конфигурации запуска и эксперимента
                RunConfiguration runConfiguration = reverseSearch(runConfigurationToFuturesToResultsMap, future);
                ExperimentConfiguration experimentConfiguration = reverseSearch(experimentConfigurationToFuturesMap, future);
                // берутся полученные результаты
                FitResults fitResults = getFromFuture(future);

                processedFutures.add(future);  // запоминается факт обработки результата

                // обработка результатов для каждого запуска
                tryToPrint(appProperties.getPrintConfigurationEach(), runConfiguration, fitResults,
                        String.format("Результаты обучения для конфигурации запуска [%s] эксперимента [%s] \n",
                                runConfiguration.getDescription(), experimentConfiguration.getDescription()),
                        appProperties.isPrintRequired(), appProperties.isDebugMode(), appProperties.getDoubleFormat());

                tryToSave(appProperties.isSaveRequired() && appProperties.isSaveConfigurationEach(),
                        runConfiguration, fitResults, appProperties.getSaveFilenamePattern(),
                        appProperties.getSaveFolder(), appProperties.getSaveSerializationType(),
                        appProperties.getDoubleFormat());

                // если обрабатываемый результат был последним для соответствующей конфигурации запуска,
                // то есть получены все результаты для соответствующей конфигурации
                if (allFuturesDone(runConfigurationToFuturesToResultsMap.get(runConfiguration))) {
                    // тогда начинается обработка результатов для данной конфигурации
                    Future<FitResults> bestFuture = findBestFuture(runConfigurationToFuturesToResultsMap.get(runConfiguration));
                    FitResults bestFitResults = getFromFuture(bestFuture);

                    tryToPrint(appProperties.getPrintConfigurationBest(), runConfiguration, bestFitResults,
                            String.format("Наилучшие результаты обучения для конфигурации запуска [%s] эксперимента [%s] \n",
                                    runConfiguration.getDescription(), experimentConfiguration.getDescription()),
                            appProperties.isPrintRequired(), appProperties.isDebugMode(), appProperties.getDoubleFormat());

                    tryToSave(appProperties.isSaveRequired() && appProperties.isSaveConfigurationBest(),
                            runConfiguration, bestFitResults, appProperties.getSaveFilenamePattern(),
                            appProperties.getSaveFolder(), appProperties.getSaveSerializationType(),
                            appProperties.getDoubleFormat());

                    logTimeSpent(runConfigurationToFuturesToResultsMap.get(runConfiguration),
                            String.format("Запуск конфигурации [%s} эксперимента [%s] занял",
                                    runConfiguration.getDescription(), experimentConfiguration.getDescription()));
                }

                if (allFuturesDone(experimentConfigurationToFuturesMap.get(experimentConfiguration))) {
                    // аналогичная проверка перед обработкой результатов для эксперимента, соответствующему
                    // полученному результату
                    Future<FitResults> bestFuture = findBestFuture(experimentConfigurationToFuturesMap.get(experimentConfiguration));
                    RunConfiguration bestRunConfiguration = reverseSearch(runConfigurationToFuturesToResultsMap, bestFuture);
                    FitResults bestFitResults = getFromFuture(bestFuture);

                    tryToPrint(appProperties.getPrintExperimentBest(), bestRunConfiguration, bestFitResults,
                            String.format("Наилучшие результаты обучения для всех конфигураций запуска эксперимента [%s]" +
                                            " (соответствуют конфигурации [%s]) \n",
                                    experimentConfiguration.getDescription(), bestRunConfiguration.getDescription()),
                            appProperties.isPrintRequired(), appProperties.isDebugMode(), appProperties.getDoubleFormat());

                    tryToSave(appProperties.isSaveRequired() && appProperties.isSaveExperimentBest(),
                            bestRunConfiguration, bestFitResults, appProperties.getSaveFilenamePattern(),
                            appProperties.getSaveFolder(), appProperties.getSaveSerializationType(),
                            appProperties.getDoubleFormat());

                    logTimeSpent(experimentConfigurationToFuturesMap.get(experimentConfiguration),
                            String.format("Выполнение эксперимента [%s] заняло",
                                    experimentConfiguration.getDescription()));
                }
            }  // завершение обработки полученных результатов
            Utils.myWait(1_000);  // таймаут перед проверкой наличия новых результатов
        }

        // после получения всех результатов обязательно завершается executorService, иначе программа никогда не
        // завершится, то есть станет демоном
        executorService.shutdown();

        // обработка результатов для всех экспериментов
        Future<FitResults> bestFuture = findBestFuture(allFutures);
        RunConfiguration bestRunConfiguration = reverseSearch(runConfigurationToFuturesToResultsMap, bestFuture);
        ExperimentConfiguration bestExperimentConfiguration = reverseSearch(experimentConfigurationToFuturesMap, bestFuture);
        FitResults bestFitResults = getFromFuture(bestFuture);

        tryToPrint(appProperties.getPrintExperimentBest(), bestRunConfiguration, bestFitResults,
                String.format("Наилучшие результаты обучения для всех конфигураций запуска всех экспериментов " +
                                " (соответствуют конфигурации [%s] эксперимента [%s]) \n",
                        bestRunConfiguration.getDescription(), bestExperimentConfiguration.getDescription()),
                appProperties.isPrintRequired(), appProperties.isDebugMode(), appProperties.getDoubleFormat());

        tryToSave(appProperties.isSaveRequired() && appProperties.isSaveExperimentBest(),
                bestRunConfiguration, bestFitResults, appProperties.getSaveFilenamePattern(),
                appProperties.getSaveFolder(), appProperties.getSaveSerializationType(),
                appProperties.getDoubleFormat());

        logTimeSpent(processedFutures, "Выполнение всех экспериментов заняло");

        logger.fine("Завершение программы. Программа работала - " +
                Utils.millisToHMS(System.currentTimeMillis() - startTime));
    }

    private static <T> boolean allFuturesDone(Set<Future<T>> futures) {
        return futures.stream()
                .map(Future::isDone)
                .reduce((done1, done2) -> done1 && done2)
                .orElse(false);
    }

    private static <K, V> K reverseSearch(Map<K, Set<V>> map, V value) {
        for (Map.Entry<K, Set<V>> entry: map.entrySet())
            if (entry.getValue().contains(value))
                return entry.getKey();
        throw new IllegalArgumentException("Не найден ключ по значению: " + value);
    }

    private static void tryToPrint(PrintOptions printOptions, RunConfiguration runConfiguration, FitResults fitResults,
                                   String prompt, boolean printRequired, boolean debugMode, String doubleFormat) {
        if (printRequired && printOptions.isRequired()) {
            logger.info(prompt +
                    Utils.runConfigurationAndFitResultsToString(
                            runConfiguration, fitResults, printOptions,
                            debugMode, doubleFormat));
        }
    }

    private static void tryToSave(boolean saveRequired, RunConfiguration runConfiguration, FitResults fitResults,
                                  String saveFilenamePattern, String saveFolder, SerializationType serializationType,
                                  String doubleFormat) {
        if (saveRequired) {
            String filename = String.format(saveFilenamePattern,
                    UUID.randomUUID().toString().substring(0, 5) + '_' + System.currentTimeMillis());
            logger.info("Сохранение нейросети в файл: " + filename);
            try {
                SerializationUtils.save(fitResults.getNetwork(), saveFolder, filename,
                        serializationType, doubleFormat);
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    private static <T> T getFromFuture(Future<T> future) {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.severe("Ошибка при обработке результатов: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private static void logTimeSpent(Set<Future<FitResults>> futures, String prompt) {
        long totalTimeSpent = futures
                .stream()
                .map(Main::getFromFuture)
                .mapToLong(FitResults::getTimeSpent)
                .sum();

        logger.fine(String.format("%s суммарно - %s", prompt, Utils.millisToHMS(totalTimeSpent)));

        logger.fine(String.format("%s в среднем - %s",
                prompt, Utils.millisToHMS(totalTimeSpent / futures.size())));
    }

    private static Future<FitResults> findBestFuture(Set<Future<FitResults>> futures) {
        return futures.stream()
                .min(Comparator.comparingDouble(future -> getFromFuture(future).getMaxAbsoluteError()))
                .orElseThrow(() -> new RuntimeException("Ошибка при обработке результатов"));
    }

}

