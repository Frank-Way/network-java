package models.data;

import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;

/**
 * Параметры для загрузки данных
 */
@YamlSerializable
public abstract class LoadParameters implements DeepCopyable, Serializable {
    @Override
    public abstract LoadParameters deepCopy();

    @Override
    public abstract String toString();
}
