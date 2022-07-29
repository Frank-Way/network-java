package utils;

import com.sun.istack.internal.NotNull;
import models.exceptions.SerializationException;
import models.interfaces.Debuggable;
import models.trainers.FitResults;
import options.Constants;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * Класс с описанием эксперимента для исследования зависимости влияния отдельных параметров. Представляет собой
 *  обёртку вокруг набора {@link RunConfiguration}. Подразумевается, что в одном эксперименте задаются связанные
 *  {@link RunConfiguration} с разными параметрами, влияние которых исследуется в рамках эксперимента.
 * Параметры модели:
 *  {@link Logger} - логгер;
 *  {@link MyId} - идентификатор;
 *  description - описание (например, "Влияние размера выборки");
 *  список<{@link RunConfiguration}> - набор конфигураций, запускаемых в рамках эксперимента;
 *  мапа<{@link RunConfiguration}, список<{@link FitResults}>> - отражение результатов обучения для каждой конфигурации;
 *  мапа<{@link RunConfiguration}, <{@link FitResults}> - отражение наилучших результатов обучения для каждой конфигурации;
 */
public class ExperimentConfiguration implements Debuggable {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final MyId myId;

    private final String description;

    private final List<RunConfiguration> runConfigurations;
    private final Map<RunConfiguration, List<FitResults>> resultsMap;
    private final Map<RunConfiguration, FitResults> bestResultsMap;

    /**
     * Конструктор
     * @param description  описание эксперимента (например, "Влияние размера выборки")
     * @param runConfigurations  набор {@link RunConfiguration} для запуска эксперимента
     */
    public ExperimentConfiguration(String description, @NotNull List<RunConfiguration> runConfigurations) {
        this.runConfigurations = runConfigurations;
        this.description = description;
        resultsMap = new HashMap<>();
        bestResultsMap = new HashMap<>();
        for (RunConfiguration runConfiguration: runConfigurations) {
            resultsMap.put(runConfiguration, new ArrayList<>());
            bestResultsMap.put(runConfiguration, null);
        }
        myId = new MyId(UUID.randomUUID().toString(), null, hashCode() + "");
    }

    public MyId getMyId() {
        return myId;
    }

    private List<RunConfiguration> getRunConfigurations() {
        return runConfigurations;
    }

    public RunConfiguration getRunConfiguration(int index) {
        return runConfigurations.get(index);
    }

    public void addRunConfiguration(@NotNull RunConfiguration runConfiguration) {
        runConfigurations.add(runConfiguration);
    }

    public void addRunConfiguration(int index, @NotNull RunConfiguration runConfiguration) {
        runConfigurations.add(index, runConfiguration);
    }

    public int runConfigurationsCount() {
        return runConfigurations.size();
    }

    public String getDescription() {
        return description;
    }

    public Map<RunConfiguration, List<FitResults>> getResultsMap() {
        return resultsMap;
    }

    public Map<RunConfiguration, FitResults> getBestResultsMap() {
        return bestResultsMap;
    }

    /**
     * Запуск эксперимента
     */
    public void run() {
        logger.fine(String.format("Запуск эксперимента \"%s\"", description));
        logger.finer("Эксперимент: " + toString(Constants.DEBUG_MODE));

        // формирование набора потоков, в которых будут запущены отдельные эксперименты
        List<ExperimentConfigRunner> threads = runConfigurations.stream()
                .map(runConfiguration -> new ExperimentConfigRunner(runConfiguration, myId))
                .collect(Collectors.toList());

        Utils.startThreads(threads);  // запуск потоков

        Utils.joinThreads(threads);  // ожидание завершения потоков

        // получение результатов для каждой конфигурации
        for (ExperimentConfigRunner thread: threads) {
            resultsMap.get(thread.getRunConfiguration()).addAll(thread.getFitResults());
            bestResultsMap.put(thread.getRunConfiguration(), thread.getBestFitResults());
        }

        // получение наилучших результатов по всем конфигурациям эксперимента
        ExperimentConfigRunner bestResultsThread = threads.stream()
                .min(Comparator.comparingDouble(r -> r.getBestFitResults().getMaxAbsoluteError()))
                .orElseThrow(() -> new RuntimeException(
                        String.format("Ошибка при получении результатов эксперимента \"%s\"", description)));

        // вывод
        if (Constants.PRINT_REQUIRED && Constants.PRINT_EXPERIMENT_BEST.isRequired())
            logger.info(String.format("Наилучшие результаты обучения для всех конфигураций [эксперимент \"%s\"]\n", description) +
                    Utils.runConfigurationAndFitResultsToString(bestResultsThread.getRunConfiguration(),
                            bestResultsThread.getBestFitResults(), Constants.PRINT_EXPERIMENT_BEST,
                            Constants.DEBUG_MODE, Constants.TABLE_PART, Constants.DOUBLE_FORMAT));

        // сохранение сети
        if (Constants.SAVE_REQUIRED && Constants.SAVE_EXPERIMENT_BEST) {
            bestResultsThread.getBestFitResults().getNetwork().clear();
            try {
                Utils.save(bestResultsThread.getBestFitResults().getNetwork(), Constants.SAVE_FOLDER,
                        String.format(Constants.SAVE_NETWORK_PATTERN,
                                bestResultsThread.getRunConfiguration().getMyId().getUid() + '_' + System.currentTimeMillis()));
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
        logger.fine(String.format("Завершение эксперимента \"%s\"", description));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentConfiguration that = (ExperimentConfiguration) o;
        return Objects.equals(myId, that.myId) &&
               Objects.equals(description, that.description) &&
               Objects.equals(runConfigurations, that.runConfigurations) &&
               Objects.equals(resultsMap, that.resultsMap) &&
               Objects.equals(bestResultsMap, that.bestResultsMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, runConfigurations, resultsMap, bestResultsMap);
    }

    @Override
    public String toString() {
        return "ExperimentConfiguration{" +
                "myId=" + myId +
                ", description='" + description + '\'' +
                ", runConfigurations=" + runConfigurations +
                ", resultsMap=" + resultsMap +
                ", bestResultsMap=" + resultsMap +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "КонфигурацияЭксперимента{" +
                "myId=" + myId +
                ", описание='" + description + '\'' +
                ", конфигурацииЗапуска=" + runConfigurations.stream().map(rc -> rc.toString(debugMode)).collect(Collectors.toList()) +
                ", наилучшиеРезультаты=" + Utils.mapToDebugString(bestResultsMap, debugMode) +
                '}';
    }
}
