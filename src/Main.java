import models.exceptions.SerializationException;
import options.Constants;
import utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

public class Main {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
        try {
            LogManager logManager = LogManager.getLogManager();
            Class<Main> aClass = Main.class;
            InputStream inputStream = aClass.getResourceAsStream("logging.properties");
            logManager.readConfiguration(inputStream);
            logger.fine("Успешно считаны конфигурации для логеров");
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }


        List<ExperimentConfiguration> experimentConfigurations = ExperimentConfigurations.experimentConfigurations;
        
        logger.fine("Успешно считаны конфигурации экспериментов");
        logger.finer(experimentConfigurations.stream()
                .map(ec -> ec.toString(Constants.DEBUG_MODE))
                .collect(Collectors.toList()).toString());

        logger.fine("Начало запуска экспериментов");
        for (ExperimentConfiguration experimentConfiguration: experimentConfigurations) {
            experimentConfiguration.run();
            try {
                Thread.sleep(1_000);
            } catch (InterruptedException e) {
                logger.severe("Ошибка ожидания: " + e.getMessage());
            }
        }

        double bestError = Double.MAX_VALUE;
        RunConfiguration bestRunConfiguration = null;
        TrainResults bestTrainResults = null;
        for (ExperimentConfiguration experimentConfiguration: experimentConfigurations) {
            for (int i = 0; i < experimentConfiguration.runConfigurationsCount(); i++) {
                RunConfiguration runConfiguration = experimentConfiguration.getRunConfiguration(i);
                double error = experimentConfiguration.getBestResultsMap().get(runConfiguration).getMaxAbsoluteError();
                if (error < bestError) {
                    bestError = error;
                    bestRunConfiguration = runConfiguration;
                    bestTrainResults = experimentConfiguration.getBestResultsMap().get(bestRunConfiguration);
                }
            }
        }
        if (bestRunConfiguration == null || bestTrainResults == null)
            throw new RuntimeException("Ошибка при обработке результатов обучения");

        if (Constants.PRINT_REQUIRED && Constants.PRINT_ALL_EXPERIMENTS_BEST.isRequired())
            logger.info(Utils.runConfigurationAndTrainResultsToString(
                    "Наилучшие результаты обучения для всех конфигураций экспериментов",
                    bestRunConfiguration, bestTrainResults, Constants.PRINT_ALL_EXPERIMENTS_BEST, Constants.DEBUG_MODE,
                    Constants.TABLE_PART, Constants.DOUBLE_FORMAT));

        if (Constants.SAVE_REQUIRED && Constants.SAVE_ALL_EXPERIMENTS_BEST) {
            bestTrainResults.getNetwork().clear();
            try {
                Utils.save(bestTrainResults.getNetwork(), Constants.SAVE_FOLDER,
                        String.format(Constants.SAVE_NETWORK_PATTERN,
                                bestRunConfiguration.getMyId().getUid() + '_' + System.currentTimeMillis()));
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
        logger.fine("Завершение запуска экспериментов");
    }

}

