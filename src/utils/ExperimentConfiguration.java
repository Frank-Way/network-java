package utils;

import com.sun.istack.internal.NotNull;
import models.interfaces.Debuggable;
import models.trainers.FitResults;

import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Класс с описанием эксперимента для исследования зависимости влияния отдельных параметров. Представляет собой
 *  обёртку вокруг набора {@link RunConfiguration}. Подразумевается, что в одном эксперименте задаются связанные
 *  {@link RunConfiguration} с разными параметрами, влияние которых исследуется в рамках эксперимента.
 * Параметры модели:
 *  {@link Logger} - логгер;
 *  description - описание (например, "Влияние размера выборки");
 *  список<{@link RunConfiguration}> - набор конфигураций, запускаемых в рамках эксперимента;
 *  мапа<{@link RunConfiguration}, список<{@link FitResults}>> - отражение результатов обучения для каждой конфигурации;
 *  мапа<{@link RunConfiguration}, <{@link FitResults}> - отражение наилучших результатов обучения для каждой конфигурации;
 */
public class ExperimentConfiguration implements Debuggable {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    private final String description;
    private final List<RunConfiguration> runConfigurations;

    /**
     * Конструктор
     * @param description  описание эксперимента (например, "Влияние размера выборки")
     * @param runConfigurations  набор {@link RunConfiguration} для запуска эксперимента
     */
    public ExperimentConfiguration(String description, @NotNull List<RunConfiguration> runConfigurations) {
        this.runConfigurations = runConfigurations;
        this.description = description;
    }

    public List<RunConfiguration> getRunConfigurations() {
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
    @Override
    public String toString() {
        return "ExperimentConfiguration{" +
                "description='" + description + '\'' +
                ", runConfigurations=" + runConfigurations +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "КонфигурацияЭксперимента{" +
                "описание='" + description + '\'' +
                ", конфигурацииЗапуска=" + runConfigurations.stream().map(rc -> rc.toString(debugMode)).collect(Collectors.toList()) +
                '}';
    }
}
