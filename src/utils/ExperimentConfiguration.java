package utils;

import com.sun.tools.javac.util.Pair;
import models.trainers.FitResults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ExperimentConfiguration implements Debuggable {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final List<RunConfiguration> runConfigurations;
    private final String description;
    private final Map<RunConfiguration, TrainResults> resultsMap;
    private final Map<RunConfiguration, TrainResults> bestResultsMap;
    private final Map<RunConfiguration, TrainResults> bestResultsPerConfigurationMap;

    public ExperimentConfiguration(String description, List<RunConfiguration> runConfigurations) {
        this.runConfigurations = runConfigurations;
        this.description = description;
        resultsMap = new HashMap<>();
        bestResultsMap = new HashMap<>();
        bestResultsPerConfigurationMap = new HashMap<>();
    }

    public List<RunConfiguration> getRunConfigurations() {
        return runConfigurations;
    }

    public String getDescription() {
        return description;
    }

    public Map<RunConfiguration, TrainResults> getResultsMap() {
        return resultsMap;
    }

    public Map<RunConfiguration, TrainResults> getBestResultsMap() {
        return bestResultsMap;
    }

    public Map<RunConfiguration, TrainResults> getBestResultsPerConfigurationMap() {
        return bestResultsPerConfigurationMap;
    }

    public Map<RunConfiguration, TrainResults> run() {
        logger.info(String.format("Запуск эксперимента \"%s\"", description));
        List<Pair<RunConfiguration, TrainResults>> resultsPerRunConfigurations = new ArrayList<>();
        for (int i = 0; i < runConfigurations.size(); i++) {
            RunConfiguration runConfiguration = runConfigurations.get(i);
            List<Pair<RunConfiguration, TrainResults>> resultsPerRunConfiguration = new ArrayList<>();
            logger.fine(String.format("Начало обработки %d конфигурации (всего будет выполнено %d перезапусков)",
                    i + 1, runConfiguration.getRetries()));
            logger.finer(runConfiguration.toString(OverallConstants.DEBUG_MODE));
            for (int retry = 0; retry < runConfiguration.getRetries(); retry++) {
                logger.fine(String.format("%d запуск обучения для %d конфигурации", i + 1, retry + 1));
                RunConfiguration clone = runConfiguration.clone();
                FitResults fitResults = clone.getTrainer().fit(clone.getFitParameters());
                TrainResults results = new TrainResults(clone.getTrainer().getNetwork(), clone.getFitParameters().getDataset(), fitResults);
                logger.fine(String.format("%d завершение обучения для %d конфигурации", i + 1, retry + 1));
                logger.finer(results.toString(OverallConstants.DEBUG_MODE));
                resultsMap.put(clone, results);
                Pair<RunConfiguration, TrainResults> resultPerRunConfiguration = new Pair<>(clone, results);
                resultsPerRunConfiguration.add(resultPerRunConfiguration);
                Utils.print(String.format("Результаты обучения для конфигурации [[%d.%d]] [эксперимент \"%s\"]",
                                i, retry, description),
                        resultPerRunConfiguration.fst, resultPerRunConfiguration.snd,
                        OverallConstants.PRINT_REQUIRED && OverallConstants.PRINT_EACH_CONFIGURATION_REQUIRED,
                        OverallConstants.PRINT_EACH_CONFIGURATION_PARAMETERS_REQUIRED,
                        OverallConstants.PRINT_EACH_CONFIGURATION_TABLE_REQUIRED,
                        OverallConstants.PRINT_EACH_CONFIGURATION_DYNAMIC_REQUIRED);

            }
            Pair<RunConfiguration, TrainResults> bestResultPerRunConfiguration = resultsPerRunConfiguration.get(0);
            for (Pair<RunConfiguration, TrainResults> resultPerRunConfiguration : resultsPerRunConfiguration)
                if (resultPerRunConfiguration.snd.getMaxAbsoluteError() < bestResultPerRunConfiguration.snd.getMaxAbsoluteError())
                    bestResultPerRunConfiguration = resultPerRunConfiguration;
            bestResultsPerConfigurationMap.put(bestResultPerRunConfiguration.fst, bestResultPerRunConfiguration.snd);
            resultsPerRunConfigurations.add(bestResultPerRunConfiguration);
            Utils.print(String.format("Наилучшие результаты обучения для конфигурации [[%d]] [эксперимент \"%s\"]",
                            i, description),
                    bestResultPerRunConfiguration.fst, bestResultPerRunConfiguration.snd,
                    OverallConstants.PRINT_REQUIRED && OverallConstants.PRINT_EACH_CONFIGURATION_BEST_REQUIRED,
                    OverallConstants.PRINT_EACH_CONFIGURATION_BEST_PARAMETERS_REQUIRED,
                    OverallConstants.PRINT_EACH_CONFIGURATION_BEST_TABLE_REQUIRED,
                    OverallConstants.PRINT_EACH_CONFIGURATION_BEST_DYNAMIC_REQUIRED);
        }
        Pair<RunConfiguration, TrainResults> bestResultPerRunConfigurations = resultsPerRunConfigurations.get(0);
        for (Pair<RunConfiguration, TrainResults> resultPerRunConfigurations : resultsPerRunConfigurations) {
            if (resultPerRunConfigurations.snd.getMaxAbsoluteError() < bestResultPerRunConfigurations.snd.getMaxAbsoluteError())
                bestResultPerRunConfigurations = resultPerRunConfigurations;
        }
        bestResultsMap.put(bestResultPerRunConfigurations.fst, bestResultPerRunConfigurations.snd);
        Utils.print(String.format("Наилучшие результаты обучения для всех конфигураций [эксперимент \"%s\"]", description),
                bestResultPerRunConfigurations.fst, bestResultPerRunConfigurations.snd,
                OverallConstants.PRINT_REQUIRED && OverallConstants.PRINT_ALL_CONFIGURATIONS_BEST_REQUIRED,
                OverallConstants.PRINT_ALL_CONFIGURATIONS_BEST_PARAMETERS_REQUIRED,
                OverallConstants.PRINT_ALL_CONFIGURATIONS_BEST_TABLE_REQUIRED,
                OverallConstants.PRINT_ALL_CONFIGURATIONS_BEST_DYNAMIC_REQUIRED);
        return resultsMap;
    }

    @Override
    public String toString() {
        return "ExperimentConfiguration{" +
                "description='" + description + '\'' +
                ", runConfigurations=" + runConfigurations +
                ", resultsMap=" + resultsMap +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "КонфигурацияЭксперимента{" +
                "описание='" + description + '\'' +
                ", конфигурацииЗапуска=" + runConfigurations.stream().map(rc -> rc.toString(debugMode)).collect(Collectors.toList()) +
                ", результаты=" + Utils.mapToDebugString(resultsMap, debugMode) +
                '}';
    }
}
