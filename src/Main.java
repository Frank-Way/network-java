import models.exceptions.SerializationException;
import models.trainers.FitResults;
import options.Constants;
import utils.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.logging.*;
import java.util.stream.Collectors;

/**
 * Основная программа для обучения сетей в соответствии с экспериментами, описанными в {@link ExperimentConfigurations}
 */
public class Main {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static void main(String[] args) {
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


        List<ExperimentConfiguration> experimentConfigurations = ExperimentConfigurations.getExperimentConfigurations();
        
        logger.fine("Успешно считаны конфигурации экспериментов");
        logger.finer(experimentConfigurations.stream()
                .map(ec -> ec.toString(Constants.DEBUG_MODE))
                .collect(Collectors.toList()).toString());

        logger.fine("Начало запуска экспериментов");
        for (ExperimentConfiguration experimentConfiguration: experimentConfigurations)
            experimentConfiguration.run();

        logger.fine("Начало обработки результатов экспериментов");
        double bestError = Double.MAX_VALUE;
        RunConfiguration bestRunConfiguration = null;
        FitResults bestFitResults = null;
        // поиск конфигурации и сети с наилучшими результатами
        for (ExperimentConfiguration experimentConfiguration: experimentConfigurations) {
            for (int i = 0; i < experimentConfiguration.runConfigurationsCount(); i++) {
                RunConfiguration runConfiguration = experimentConfiguration.getRunConfiguration(i);
                double error = experimentConfiguration.getBestResultsMap().get(runConfiguration).getMaxAbsoluteError();
                if (error < bestError) {
                    bestError = error;
                    bestRunConfiguration = runConfiguration;
                    bestFitResults = experimentConfiguration.getBestResultsMap().get(bestRunConfiguration);
                }
            }
        }
        if (bestRunConfiguration == null || bestFitResults == null)
            throw new RuntimeException("Ошибка при обработке результатов обучения");

        if (Constants.PRINT_REQUIRED && Constants.PRINT_ALL_EXPERIMENTS_BEST.isRequired())
            logger.info("Наилучшие результаты обучения для всех конфигураций экспериментов\n" +
                    Utils.runConfigurationAndFitResultsToString(
                            bestRunConfiguration, bestFitResults, Constants.PRINT_ALL_EXPERIMENTS_BEST,
                            Constants.DEBUG_MODE, Constants.TABLE_PART, Constants.DOUBLE_FORMAT));

        if (Constants.SAVE_REQUIRED && Constants.SAVE_ALL_EXPERIMENTS_BEST) {
            bestFitResults.getNetwork().clear();  // очистка сети перед сериализацией для снижения "веса" файла
            String filename = String.format(Constants.SAVE_NETWORK_PATTERN,
                    bestRunConfiguration.getMyId().getUid() + '_' + System.currentTimeMillis());
            logger.info("Сохранение нейросети в файл: " + filename);
            try {
                Utils.save(bestFitResults.getNetwork(), Constants.SAVE_FOLDER, filename);
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
        logger.fine("Завершение запуска экспериментов");
    }

}

