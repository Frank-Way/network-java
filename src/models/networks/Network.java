package models.networks;

import models.layers.Layer;
import models.losses.Loss;
import models.math.Matrix;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * Нейросеть, представленная как набор слоёв и потеря. Атрибуты модели:
 * <pre><ul>
 *  <li>набор<{@link Layer}> - набор слоёв;</li>
 *  <li>{@link Loss}         - потеря для оценки работы сети</li>
 * </ul></pre>
 */
@YamlSerializable
public class Network implements DeepCopyable, Serializable {
    @YamlField private final Layer[] layers;
    @YamlField private final Loss loss;

    /**
     * Конструктор
     * @param layers набор слоёв
     * @param loss   потеря
     */
    public Network(Layer[] layers, Loss loss) {
        this.layers = layers;
        this.loss = loss;
    }

    /**
     * Конструктор для сериализации
     */
    private Network() {
        this(null, null);
    }

    /**
     * Прямой проход сети по всем слоям
     * @param inputs вход
     * @return       выход
     */
    public Matrix forward(Matrix inputs) {
        Matrix result = inputs.deepCopy();
        for (Layer layer: layers)
            result = layer.forward(result);
        return result;
    }

    /**
     * Обратный проход сети по всем слоям (в обратном направлении)
     * @param lossGradient градиент на выходе сети (градиент потери)
     * @return             градиент на входе сети
     */
    public Matrix backward(Matrix lossGradient) {
        Matrix result = lossGradient.deepCopy();
        for (int i = 0; i < layers.length; i++)
            result = layers[layers.length - 1 - i].backward(result);
        return result;
    }

    /**
     * Оценка (вычисление потери)
     * @param inputs  входы
     * @param targets требуемые выходы
     * @return        потеря
     */
    public double calculateLoss(Matrix inputs, Matrix targets) {
        Matrix prediction = forward(inputs);
        return loss.forward(prediction, targets);
    }

    /**
     *
     * @param inputs  входы
     * @param targets требуемые выходы
     * @return        потеря
     */
    public double trainBatch(Matrix inputs, Matrix targets) {
        Matrix predictions = forward(inputs);
        double batchLoss = loss.forward(predictions, targets);
        Matrix lossGradient = loss.backward();
        backward(lossGradient);
        return batchLoss;
    }

    private Layer[] getLayers() {
        return layers;
    }

    public Layer getLayer(int index) {
        return layers[index];
    }

    public int layersCount() {
        return layers.length;
    }

    public Loss getLoss() {
        return loss;
    }

    @Override
    public Network deepCopy() {
        return new Network(Arrays.stream(layers).map(Layer::deepCopy).toArray(Layer[]::new),
                loss.deepCopy());
    }

    @Override
    public String toString() {
        return "Network{" +
                "layers=" + Arrays.toString(layers) +
                ", loss=" + loss +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return Arrays.equals(layers, network.layers) && loss.equals(network.loss);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Arrays.hashCode(layers), loss);
    }

    /**
     * Получение пустого билдера по слоям
     * @return новый билдер
     */
    public static DefaultNetworkBuilder newBuilder() {
        return new DefaultNetworkBuilder();
    }

    /**
     * Получение пустого билдера по размерам слоёв и активациям
     * @return новый билдер
     */
    public static AnotherNetworkBuilder newAnotherBuilder() {
        return new AnotherNetworkBuilder();
    }

    /**
     * Получение билдера на основе текущего экземпляра
     * @return заполненный билдер
     */
    public DefaultNetworkBuilder builder() {
        return new DefaultNetworkBuilder(this);
    }
}
