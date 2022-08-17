package utils;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import models.trainers.FitResults;
import models.trainers.Trainer;
import options.Constants;
import serialization.SerializationType;
import serialization.SerializationUtils;

import java.util.logging.Logger;

/**
 * Обёртка вокруг {@link Thread} для запуска обучения по одной конфигурации. Параметры модели:
 *  {@link Logger} - логгер;
 *  NAME_PATTERN - шаблон имени потока;
 *  {@link MyId} - идентификатор потока;
 *  {@link RunConfiguration} - конфигурация, обучение по которой будет запущена в данном потоке;
 *  {@link FitResults} - результат обучения, который будет сформирован после запуска потока.
 */
public class RunConfigRunner extends Thread {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private static final String NAME_PATTERN = "single-thread-%s";
    private final MyId myId;

    private final RunConfiguration runConfiguration;
    private FitResults fitResults;

    /**
     * Конструктор
     * @param runConfiguration  конфигурация запуска
     * @param parentMyId  идентификатор родителя, кто создал данный поток
     */
    public RunConfigRunner(@NotNull RunConfiguration runConfiguration, @NotNull MyId parentMyId) {
        this.runConfiguration = runConfiguration;
        myId = MyId.buildNewFromParent(parentMyId);
        setName(String.format(NAME_PATTERN, myId.toThreadId()));
    }

    public FitResults getFitResults() {
        return fitResults;
    }

    public MyId getMyId() {
        return myId;
    }

    /**
     * Запуск обучения. По завершении результаты доступны через метод getTrainResults()
     */
    @Override
    public void run() {
        logger.fine("Запуск потока");

        this.fitResults = Trainer.fit(runConfiguration.getFitParameters());  // запуск обучения

        // вывод
        if (Constants.PRINT_REQUIRED && Constants.PRINT_EACH_CONFIGURATION.isRequired())
            logger.info(String.format("Результаты для конфигурации [[%s]]\n", runConfiguration.getDescription()) +
                    Utils.runConfigurationAndFitResultsToString(runConfiguration, this.fitResults,
                            Constants.PRINT_EACH_CONFIGURATION, Constants.DEBUG_MODE,
                            Constants.TABLE_PART, Constants.DOUBLE_FORMAT));

        // сохранение сети
        if (Constants.SAVE_REQUIRED && Constants.SAVE_EACH_CONFIGURATION) {
            try {
                SerializationUtils.save(this.fitResults.getNetwork(), Constants.SAVE_FOLDER,
                        String.format(Constants.SAVE_NETWORK_PATTERN,
                                runConfiguration.getMyId().getUid() + '_' + System.currentTimeMillis()),
                        Constants.SERIALIZATION_TYPE);
            } catch (SerializationException e) {
                logger.severe(e.getMessage());
            }
        }
        logger.fine("Завершение потока");
    }
}
