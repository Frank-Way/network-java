package utils.automatization;

import models.trainers.FitResults;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;

import java.util.Arrays;
import java.util.logging.Logger;

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
@YamlSerializable
public class ExperimentConfiguration {
    private static final transient Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    @YamlField private final String description;
    @YamlField private final RunConfiguration[] runConfigurations;

    /**
     * Конструктор
     * @param description  описание эксперимента (например, "Влияние размера выборки")
     * @param runConfigurations  набор {@link RunConfiguration} для запуска эксперимента
     */
    public ExperimentConfiguration(String description, RunConfiguration[] runConfigurations) {
        this.runConfigurations = runConfigurations;
        this.description = description;
    }

    private ExperimentConfiguration() {
        this(null, null);
    }

    public RunConfiguration[] getRunConfigurations() {
        return runConfigurations;
    }

    public RunConfiguration getRunConfiguration(int index) {
        return runConfigurations[index];
    }

    public int runConfigurationsCount() {
        return runConfigurations.length;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "ExperimentConfiguration{" +
                "description='" + description + '\'' +
                ", runConfigurations=" + Arrays.toString(runConfigurations) +
                '}';
    }
}
