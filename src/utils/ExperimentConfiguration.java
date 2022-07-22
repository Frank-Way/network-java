package utils;

import com.sun.istack.internal.NotNull;
import models.interfaces.Debuggable;
import options.OverallConstants;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.UUID;

public class ExperimentConfiguration implements Debuggable {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final MyId myId;

    private final String description;

    private final List<RunConfiguration> runConfigurations;
    private final Map<RunConfiguration, List<TrainResults>> resultsMap;
    private final Map<RunConfiguration, TrainResults> bestResultsMap;

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

    public Map<RunConfiguration, List<TrainResults>> getResultsMap() {
        return resultsMap;
    }

    public Map<RunConfiguration, TrainResults> getBestResultsMap() {
        return bestResultsMap;
    }

    public void run() {
        logger.fine(String.format("Запуск эксперимента \"%s\"", description));
        logger.finer("Эксперимент: " + toString(OverallConstants.DEBUG_MODE));

        HashSet<ExperimentConfigRunner> threads = new HashSet<>();
        for (RunConfiguration runConfiguration: runConfigurations) {
            ExperimentConfigRunner thread = new ExperimentConfigRunner(runConfiguration, myId);
            thread.setName(String.format(ExperimentConfigRunner.NAME_PATTERN, thread.getMyId().toThreadId()));
            threads.add(thread);
            thread.start();
        }

        Utils.joinThreads(threads);

        for (ExperimentConfigRunner thread: threads) {
            resultsMap.get(thread.getRunConfiguration()).addAll(thread.getTrainResults());
            bestResultsMap.put(thread.getRunConfiguration(), thread.getBestTrainResults());
        }

        ExperimentConfigRunner bestResultsThread = threads.stream()
                .min(Comparator.comparingDouble(r -> r.getBestTrainResults().getMaxAbsoluteError()))
                .orElseThrow(() -> new RuntimeException(
                        String.format("Ошибка при получении результатов эксперимента \"%s\"", description)));

        Utils.print(String.format("Наилучшие результаты обучения для всех конфигураций [эксперимент \"%s\"]", description),
                bestResultsThread.getRunConfiguration(), bestResultsThread.getBestTrainResults(),
                OverallConstants.PRINT_REQUIRED && OverallConstants.PRINT_EXPERIMENT_BEST.isRequired(),
                OverallConstants.PRINT_EXPERIMENT_BEST);

        logger.fine(String.format("Завершение эксперимента \"%s\"", description));
    }

//    public void runNoExperimentThreads() {
//        logger.fine(String.format("Запуск эксперимента \"%s\"", description));
//        logger.finer("Эксперимент: " + toString(OverallConstants.DEBUG_MODE));
//
//        HashSet<RunConfigRunner> threads = new HashSet<>();
//
//        for (RunConfiguration runConfiguration: runConfigurations) {
//            for (int retry = 0; retry < runConfiguration.getRetries(); retry++) {
//
//            }
//        }
//
//        Utils.print(String.format("Наилучшие результаты обучения для всех конфигураций [эксперимент \"%s\"]", description),
//                bestResultsThread.getRunConfiguration(), bestResultsThread.getBestTrainResults(),
//                OverallConstants.PRINT_REQUIRED && OverallConstants.PRINT_EXPERIMENT_BEST.isRequired(),
//                OverallConstants.PRINT_EXPERIMENT_BEST);
//
//        logger.fine(String.format("Завершение эксперимента \"%s\"", description));
//
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExperimentConfiguration)) return false;
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
