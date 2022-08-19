package utils;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.trainers.FitParameters;

/**
 * Конфигурация запуска обучения сети. Атрибуты модели:
 *  {@link FitParameters} - параметры обучения для метода fit класса {@link models.trainers.Trainer};
 *  retries - количество перезапусков попыток обучения для данной конфигурации;
 *  description - описание конфигурации (например, "Увеличено вдвое количество эпох").
 */
public class RunConfiguration implements Copyable<RunConfiguration>, Debuggable {
    private final FitParameters fitParameters;
    private final int retries;
    private final String description;

    /**
     * Конструктор
     * @param retries  количество перезапусков
     * @param description  описание
     * @param fitParameters  параметры обучения
     */
    public RunConfiguration(int retries, String description, @NotNull FitParameters fitParameters) {
        this.fitParameters = fitParameters;
        if (retries <= 0)
            throw new IllegalArgumentException(String.format("Недопустимое количество перезапусков (retries=%d)", retries));
        this.retries = retries;
        this.description = description;
    }

    /**
     * copy constructor
     */
    private RunConfiguration(FitParameters fitParameters, int retries, String description) {
        this.fitParameters = fitParameters;
        this.retries = retries;
        this.description = description;
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
    public RunConfiguration copy() {
        return new RunConfiguration(Utils.copyNullable(fitParameters), retries, description);
    }

    @Override
    public String toString() {
        return "RunConfiguration{" +
                "fitParameters=" + fitParameters +
                ", retries=" + retries +
                ", description=" + description +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "КонфигурацияЗапуска{" +
                "параметрыОбучения=" + fitParameters.toString(debugMode) +
                ", перезапусков=" + retries +
                ", описание=" + description +
                '}';
    }
}