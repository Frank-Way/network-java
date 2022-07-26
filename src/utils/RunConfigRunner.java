package utils;

import com.sun.istack.internal.NotNull;
import models.exceptions.SerializationException;
import models.trainers.FitResults;
import models.trainers.Trainer;
import options.Constants;

import java.util.logging.Logger;

public class RunConfigRunner extends Thread{
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    public static final String NAME_PATTERN = "single-thread-%s";
    private final MyId myId;

    private final RunConfiguration runConfiguration;
    private TrainResults trainResults;

    public RunConfigRunner(@NotNull RunConfiguration runConfiguration, @NotNull MyId parentMyId) {
        this.runConfiguration = runConfiguration;
        myId = MyId.buildNewFromParent(parentMyId);
    }

    public RunConfiguration getRunConfiguration() {
        return runConfiguration;
    }

    public TrainResults getTrainResults() {
        return trainResults;
    }

    public MyId getMyId() {
        return myId;
    }

    @Override
    public void run() {
        logger.fine("Запуск потока");

        FitResults fitResults = Trainer.fit(runConfiguration.getFitParameters());
        trainResults = new TrainResults(runConfiguration.getFitParameters().getDataset(), fitResults);

        if (Constants.PRINT_REQUIRED && Constants.PRINT_EACH_CONFIGURATION.isRequired())
            logger.info(Utils.runConfigurationAndTrainResultsToString(String.format("Результаты для конфигурации [[%s]]", runConfiguration.getDescription()),
                    runConfiguration, trainResults, Constants.PRINT_EACH_CONFIGURATION, Constants.DEBUG_MODE,
                    Constants.TABLE_PART, Constants.DOUBLE_FORMAT));

        if (Constants.SAVE_REQUIRED && Constants.SAVE_EACH_CONFIGURATION) {
            trainResults.getNetwork().clear();
            try {
                Utils.save(trainResults.getNetwork(), Constants.SAVE_FOLDER,
                        String.format(Constants.SAVE_NETWORK_PATTERN,
                                runConfiguration.getMyId().getUid() + '_' + System.currentTimeMillis()));
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
        logger.fine("Завершение потока");
    }
}
