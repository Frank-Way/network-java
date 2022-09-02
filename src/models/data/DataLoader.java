package models.data;

import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;

/**
 * Загрузчик данных для обучения
 */
@YamlSerializable
public abstract class DataLoader implements DeepCopyable, Serializable {
    /**
     * Генерация выборки
     * @param parameters параметры для генерации выборки
     * @return           выборка
     */
    public Dataset load(LoadParameters parameters) {
        return new Dataset(getTrainData(parameters),
                getTestData(parameters),
                getValidData(parameters));
    }

    protected abstract Data getTrainData(LoadParameters parameters);
    protected abstract Data getTestData(LoadParameters parameters);
    protected abstract Data getValidData(LoadParameters parameters);

    @Override
    public abstract DataLoader deepCopy();
}
