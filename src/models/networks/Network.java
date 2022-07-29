package models.networks;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.layers.Layer;
import models.losses.Loss;
import models.math.Matrix;
import models.interfaces.Debuggable;
import utils.Utils;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Нейросеть, представленная как набор слоёв и потеря. Атрибуты модели:
 *  список<{@link Layer}> - набор слоёв;
 *  {@link Loss} - потеря для оценки работы сети
 */
public class Network implements Copyable<Network>, Debuggable, Serializable {
    private final List<Layer> layers;
    private final Loss loss;

    /**
     * Конструктор
     * @param layers набор слоёв
     * @param loss потеря
     */
    public Network(@NotNull List<Layer> layers, @NotNull Loss loss) {
        this.layers = layers;
        this.loss = loss;
    }

    /**
     * Прямой проход сети по всем слоям
     * @param inputs вход
     * @return выход
     */
    public Matrix forward(@NotNull Matrix inputs) {
        Matrix result = inputs.copy();
        for (Layer layer: layers)
            result = layer.forward(result);
        return result;
    }

    /**
     * Обратный проход сети по всем слоям (в обратном направлении)
     * @param lossGradient градиент на выходе сети (градиент потери)
     * @return градиент на входе сети
     */
    public Matrix backward(@NotNull Matrix lossGradient) {
        Matrix result = lossGradient.copy();
        for (int i = 0; i < layers.size(); i++)
            result = layers.get(layers.size() - 1 - i).backward(result);
        return result;
    }

    /**
     * Оценка (вычисление потери)
     * @param inputs входы
     * @param targets требуемые выходы
     * @return потеря
     */
    public double calculateLoss(@NotNull Matrix inputs, @NotNull Matrix targets) {
        Matrix prediction = forward(inputs);
        return loss.forward(prediction, targets);
    }

    /**
     *
     * @param inputs входы
     * @param targets требуемые выходы
     * @return потеря
     */
    public double trainBatch(@NotNull Matrix inputs, @NotNull Matrix targets) {
        Matrix predictions = forward(inputs);
        double batchLoss = loss.forward(predictions, targets);
        Matrix lossGradient = loss.backward();
        backward(lossGradient);
        return batchLoss;
    }

    /**
     * Очистка промежуточных результатов
     */
    public void clear() {
        loss.clear();
        layers.forEach(Layer::clear);
    }

    private List<Layer> getLayers() {
        return layers;
    }

    public Layer getLayer(int index) {
        return layers.get(index);
    }

    public void addLayer(@NotNull Layer layer) {
        layers.add(layer);
    }

    public void addLayer(int index, @NotNull Layer layer) {
        layers.add(index, layer);
    }

    public int layersCount() {
        return layers.size();
    }

    @Override
    public Network copy() {
        Loss lossCopy = Utils.copyNullable(loss);
        List<Layer> layersCopy = layers.stream().map(Utils::copyNullable).collect(Collectors.toList());

        return new Network(layersCopy, lossCopy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return Objects.equals(layers, network.layers) &&
               Objects.equals(loss, network.loss);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layers, loss);
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "layers=" + layers +
                ", loss=" + loss +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                "слои=" + layers.stream().map(layer -> layer.toString(debugMode)).collect(Collectors.toList()) +
                ", потеря=" + loss.toString(debugMode) +
                '}';
    }

    private String getClassName() {
        return "Нейросеть";
    }

    private String getDebugClassName() {
        return "Network";
    }
}
