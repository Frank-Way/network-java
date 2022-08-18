package utils;

import models.trainers.FitParameters;
import models.trainers.FitResults;
import models.trainers.Trainer;

import java.util.concurrent.Callable;

/**
 * Реализация Callable, позволяющая задать параметры обучения
 */
public class MyTask implements Callable<FitResults> {
    private final FitParameters fitParameters;

    /**
     * Конструктор
     * @param fitParameters параметры, с которыми будет запущено обучение в отдельном потоке
     */
    public MyTask(FitParameters fitParameters) {
        this.fitParameters = fitParameters;
    }

    @Override
    public FitResults call() {
        return Trainer.fit(fitParameters);
    }
}
