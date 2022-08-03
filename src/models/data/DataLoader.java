package models.data;

/**
 * Загрузчик данных для обучения
 */
public abstract class DataLoader {
    public abstract Dataset load(LoadParameters parameters);
}
