package models.data;

import models.data.approximation.NoiseMode;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;

/**
 * Параметры для загрузки данных
 */
@YamlSerializable
public abstract class LoadParameters implements DeepCopyable, Serializable {
    @YamlField
    protected final NoiseMode noiseMode;

    protected LoadParameters(NoiseMode noiseMode) {
        this.noiseMode = noiseMode;
    }

    public NoiseMode getNoiseMode() {
        return noiseMode;
    }

    @Override
    public abstract LoadParameters deepCopy();

    @Override
    public abstract String toString();
}
