package models.networks;

import models.layers.DenseLayer;
import models.layers.Layer;
import models.losses.Loss;
import models.operations.Operation;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Билдер для сети, позволяющий строить сеть путем задания отдельных конфигураций слоёв
 * (размер, функция активации) и потери для сети.
 */
@YamlSerializable
public class AnotherNetworkBuilder extends NetworkBuilder implements DeepCopyable, Serializable {
    @YamlField private int[] sizes;
    @YamlField private Operation[] activations;
    @YamlField private Loss loss;

    public AnotherNetworkBuilder() {
    }

    private AnotherNetworkBuilder(int[] sizes, Operation[] activations, Loss loss) {
        this.sizes = sizes;
        this.activations = activations;
        this.loss = loss;
    }

    public AnotherNetworkBuilder sizes(int ... sizes) {
        this.sizes = sizes;
        return this;
    }

    public AnotherNetworkBuilder activations(Operation ... activations) {
        this.activations = activations;
        return this;
    }

    public AnotherNetworkBuilder loss(Loss loss) {
        this.loss = loss;
        return this;
    }

    /**
     * Возвращает "обычный" билдер
     *
     * @return билдер
     */
    public DefaultNetworkBuilder defaultBuilder() {
        validate();
        return new DefaultNetworkBuilder().layers(getLayers()).loss(getLoss());  // в сеть попадает копия потери
    }

    @Override
    protected void validate() {
        if (sizes == null || activations == null || loss == null)
            throw new IllegalStateException("Невозможно построить сеть. Обязательно указание всех параметров");
        if (sizes.length != (1 + activations.length) || activations.length < 2)
            throw new IllegalStateException(String.format(
                    "Размеры sizes и activations должны быть равны и больше 1 (sizes=%d, activations=%d)",
                    sizes.length, activations.length));
    }

    @Override
    public AnotherNetworkBuilder deepCopy() {
        return new AnotherNetworkBuilder(
                sizes == null ? null : Arrays.copyOf(sizes, sizes.length),
                activations == null ? null : Arrays.stream(activations).map(Operation::deepCopy).toArray(Operation[]::new),
                loss == null ? null : loss.deepCopy());
    }

    @Override
    protected Loss getLoss() {
        return loss == null ? null : loss.deepCopy();
    }

    @Override
    protected Layer[] getLayers() {
        Layer[] layers = new Layer[sizes.length - 1];
        IntStream.range(0, sizes.length - 1).forEach(layer ->  // сеть строится послойно
                layers[layer] = new DenseLayer(sizes[layer],  // по заданным размерностям текущего
                        sizes[layer + 1],  // и следующего слоёв
                        activations[layer].deepCopy())  // с копией функции активации
        );
        return layers;
    }

    public int[] getSizes() {
        return sizes == null ? null : Arrays.copyOf(sizes, sizes.length);
    }

    public Operation[] getActivations() {
        return activations == null ? null : Arrays.stream(activations).map(Operation::deepCopy).toArray(Operation[]::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnotherNetworkBuilder that = (AnotherNetworkBuilder) o;
        return Arrays.equals(sizes, that.sizes) && Arrays.equals(activations, that.activations) && Objects.equals(loss, that.loss);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(loss);
        result = 31 * result + Arrays.hashCode(sizes);
        result = 31 * result + Arrays.hashCode(activations);
        return result;
    }

    @Override
    public String toString() {
        return "AnotherNetworkBuilder{" +
                "sizes=" + Arrays.toString(sizes) +
                ", activations=" + Arrays.toString(activations) +
                ", loss=" + loss +
                '}';
    }
}
