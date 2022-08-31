package models.networks;

import models.layers.Layer;
import models.losses.Loss;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;

import java.util.Arrays;
import java.util.Objects;

/**
 * Билдер для сети
 */
@YamlSerializable
public class DefaultNetworkBuilder extends NetworkBuilder {
    @YamlField private Layer[] layers;
    @YamlField private Loss loss;

    public DefaultNetworkBuilder() {
    }

    private DefaultNetworkBuilder(Layer[] layers, Loss loss) {
        this.layers = layers;
        this.loss = loss;
    }

    public DefaultNetworkBuilder layers(Layer[] layers) {
        this.layers = layers;
        return this;
    }

    public DefaultNetworkBuilder loss(Loss loss) {
        this.loss = loss;
        return this;
    }

    @Override
    protected Layer[] getLayers() {
        return layers == null ? null : Arrays.stream(layers).map(Layer::deepCopy).toArray(Layer[]::new);
    }

    @Override
    protected Loss getLoss() {
        return loss == null ? null : loss.deepCopy();
    }

    @Override
    protected void validate() {
        if (layers == null || layers.length < 1 || loss == null)
            throw new IllegalStateException("Невозможно построить сеть. Обязательно указание всех параметров");
    }

    @Override
    public DefaultNetworkBuilder deepCopy() {
        return new DefaultNetworkBuilder(getLayers(), getLoss());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultNetworkBuilder that = (DefaultNetworkBuilder) o;
        return Arrays.equals(layers, that.layers) && Objects.equals(loss, that.loss);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(loss);
        result = 31 * result + Arrays.hashCode(layers);
        return result;
    }

    @Override
    public String toString() {
        return "DefaultNetworkBuilder{" +
                "layers=" + Arrays.toString(layers) +
                ", loss=" + loss +
                '}';
    }
}
