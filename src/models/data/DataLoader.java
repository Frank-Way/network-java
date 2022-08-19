package models.data;

import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

/**
 * Загрузчик данных для обучения
 */
@YamlSerializable
public abstract class DataLoader implements DeepCopyable {
    public abstract Dataset load(LoadParameters parameters);

    @Override
    public abstract DataLoader deepCopy();
}
