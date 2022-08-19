package models.networks;

import models.layers.Layer;
import models.losses.Loss;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

@YamlSerializable
public abstract class NetworkBuilder implements DeepCopyable {
    protected NetworkBuilder() {
    }

    public Network build() {
        validate();
        return new Network(getLayers(), getLoss());
    }

    protected abstract void validate();

    protected abstract Layer[] getLayers();

    protected abstract Loss getLoss();

    public abstract NetworkBuilder deepCopy();
}
