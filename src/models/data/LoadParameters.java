package models.data;

import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

/**
 * Параметры для загрузки данных
 */
@YamlSerializable
public abstract class LoadParameters implements DeepCopyable {
    @Override
    public abstract LoadParameters deepCopy();
}
