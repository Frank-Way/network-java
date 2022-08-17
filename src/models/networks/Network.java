package models.networks;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.layers.DenseLayer;
import models.layers.Layer;
import models.losses.Loss;
import models.math.Matrix;
import models.operations.Operation;
import serialization.YamlSerializationOptions;
import serialization.YamlSerializationUtils;
import utils.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static serialization.YamlSerializationOptions.CRLF;

/**
 * Нейросеть, представленная как набор слоёв и потеря. Атрибуты модели:
 *  список<{@link Layer}> - набор слоёв;
 *  {@link Loss} - потеря для оценки работы сети
 */
public class Network implements Copyable<Network>, Debuggable, Serializable {
    private static final long serialVersionUID = 5657416029976548410L;
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
        return new Network(layers.stream().map(Utils::copyNullable).collect(Collectors.toList()),
                Utils.copyNullable(loss));
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

    /**
     * Билдер для сети
     */
    public static class Builder implements Copyable<Builder> {
        private List<Layer> layers;
        private Loss loss;

        public Builder() {}

        private Builder(List<Layer> layers, Loss loss) {
            this.layers = layers;
            this.loss = loss;
        }

        public Builder layers(List<Layer> layers) {
            this.layers = layers;
            return this;
        }

        public Builder loss(Loss loss) {
            this.loss = loss;
            return this;
        }

        public Network build() {
            validate();
            return new Network(layers, loss);
        }

        private void validate() {
            if (layers == null || layers.size() < 1 || loss == null)
                throw new IllegalStateException("Невозможно построить сеть. Обязательно указание всех параметров");
        }

        @Override
        public Builder copy() {
            return new Builder(layers.stream().map(Utils::copyNullable).collect(Collectors.toList()),
                    Utils.copyNullable(loss));
        }
    }

    /**
     * Билдер для сети, позволяющий строить сеть путем задания отдельных конфигураций слоёв
     * (размер, функция активации) и потери для сети.
     */
    public static class AnotherBuilder {
        private List<Integer> sizes;
        private List<Operation> activations;
        private Loss loss;

        public AnotherBuilder() {}

        public AnotherBuilder sizes(List<Integer> sizes) {
            this.sizes = sizes;
            return this;
        }

        public AnotherBuilder activations(List<Operation> activations) {
            this.activations = activations;
            return this;
        }

        public AnotherBuilder loss(Loss loss) {
            this.loss = loss;
            return this;
        }

        public Network build() {
            validate();
            List<Layer> layers = prepareLayers();
            return new Network(layers, loss.copy());  // в сеть попадает копия потери
        }

        /**
         * Возвращает "обычный" билдер
         * @return билдер
         */
        public Builder getBuilder() {
            validate();
            List<Layer> layers = prepareLayers();
            Network.Builder builder = new Network.Builder();
            return builder.layers(layers).loss(loss.copy());  // в сеть попадает копия потери
        }

        private void validate() {
            if (sizes == null || activations == null || loss == null)
                throw new IllegalStateException("Невозможно построить сеть. Обязательно указание всех параметров");
            if (sizes.size() != (1 + activations.size()) || activations.size() < 2)
                throw new IllegalStateException(String.format(
                        "Размеры sizes и activations должны быть равны и больше 1 (sizes=%d, activations=%d)",
                                sizes.size(), activations.size()));
        }

        private List<Layer> prepareLayers() {
            List<Layer> layers = new ArrayList<>();
            for (int layer = 0; layer < sizes.size() - 1; layer++)  // сеть строится послойно
                layers.add(new DenseLayer(sizes.get(layer),  // по заданным размерностям текущего
                        sizes.get(layer + 1),  // и следующего слоёв
                        activations.get(layer).copy()));  // с копией функции активации
            return layers;
        }
    }

    public String toYaml(int baseIndent, String doubleFormat) {
        StringBuilder sb = new StringBuilder();
        final String baseIndentString = YamlSerializationUtils.repeat(" ", baseIndent);
        sb.append(baseIndentString).append("class: ").append(this.getClass().getCanonicalName()).append(CRLF);
        sb.append(baseIndentString).append("loss: ").append(CRLF);
        sb.append(loss.toYaml(baseIndent + YamlSerializationOptions.YAML_INDENT, doubleFormat));
        sb.append(baseIndentString).append("layers: ");
        for (Layer layer: layers) {
            sb.append(CRLF).append(YamlSerializationUtils.makeListInstance(layer.toYaml(
                    baseIndent + YamlSerializationOptions.YAML_INDENT * 2, doubleFormat)));
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    public static Network fromYaml(String yaml, int baseIndent) {
        String[] lines = YamlSerializationUtils.removeFirstCharacters(yaml.split(CRLF), yaml.indexOf('c'));
        String cls = YamlSerializationUtils.getClassAsString(lines, Math.max(baseIndent - YamlSerializationOptions.YAML_INDENT, 0));
        final String[] patterns = {"class: " + cls.replace(".", "\\.") + "\\n?",
                "loss:\\s?\\n?",
                ".*",
                "layers:\\s?\\n?"};
        for (int i = 0; i < patterns.length; i++)
            if (!lines[i].matches(patterns[i]))
                throw new IllegalArgumentException("Не верный формат строки: " + lines[i]);
        Loss loss = Loss.fromYaml(String.join("\n", YamlSerializationUtils.filterByIndent(lines, 2)),
                YamlSerializationOptions.YAML_INDENT);
        List<String> layersAsStrings = YamlSerializationUtils.readListAsStringsArray(Arrays.copyOfRange(
                lines, YamlSerializationUtils.getIndexOfStringMatchingPattern(lines, patterns[3]) + 1, lines.length));
        List<Layer> layers = layersAsStrings.stream()
                .map(layerAsString ->
                        Layer.fromYaml(layerAsString, baseIndent + YamlSerializationOptions.YAML_INDENT * 2))
                .collect(Collectors.toList());

        return new Network(layers, loss);
    }
}
