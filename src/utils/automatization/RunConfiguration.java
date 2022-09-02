package utils.automatization;

import models.trainers.FitParameters;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;

/**
 * Конфигурация запуска обучения сети. Атрибуты модели:
 *  {@link FitParameters} - параметры обучения для метода fit класса {@link models.trainers.Trainer};
 *  retries - количество перезапусков попыток обучения для данной конфигурации;
 *  description - описание конфигурации (например, "Увеличено вдвое количество эпох").
 */
@YamlSerializable
public class RunConfiguration implements DeepCopyable, Serializable {
    @YamlField protected final FitParameters fitParameters;
    @YamlField protected final int retries;
    @YamlField protected final String description;

    /**
     * Конструктор
     * @param retries  количество перезапусков
     * @param description  описание
     * @param fitParameters  параметры обучения
     */
    public RunConfiguration(int retries, String description, FitParameters fitParameters) {
        this.fitParameters = fitParameters;
        if (retries <= 0)
            throw new IllegalArgumentException(String.format("Недопустимое количество перезапусков (retries=%d)", retries));
        this.retries = retries;
        this.description = description;
    }

    private RunConfiguration() {
        this(1, null, null);
    }

    public FitParameters getFitParameters() {
        return fitParameters;
    }

    public int getRetries() {
        return retries;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public RunConfiguration deepCopy() {
        return new RunConfiguration(retries, description, fitParameters.deepCopy());
    }

    @Override
    public String toString() {
        return "RunConfiguration{" +
                "fitParameters=" + fitParameters +
                ", retries=" + retries +
                ", description='" + description + '\'' +
                '}';
    }
}