package utils;

import com.sun.istack.internal.NotNull;
import models.trainers.FitResults;
import options.OverallConstants;

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

        FitResults fitResults = runConfiguration.getTrainer().fit(runConfiguration.getFitParameters());
        trainResults = new TrainResults(runConfiguration.getTrainer().getNetwork(), runConfiguration.getFitParameters().getDataset(), fitResults);

        Utils.print(String.format("Результаты для конфигурации [[%s]]", runConfiguration.getMyId()),
                runConfiguration, trainResults,
                OverallConstants.PRINT_REQUIRED && OverallConstants.PRINT_EACH_CONFIGURATION.isRequired(),
                OverallConstants.PRINT_EACH_CONFIGURATION);

        logger.fine("Завершение потока");
    }
}
