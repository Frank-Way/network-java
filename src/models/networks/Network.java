package models.networks;

import models.layers.Layer;
import models.losses.Loss;
import models.math.Matrix;
import utils.Debuggable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Network implements Cloneable, Debuggable {
    protected List<Layer> layers;
    protected Loss loss;

    public Network(List<Layer> layers, Loss loss) {
        this.layers = layers;
        this.loss = loss;
    }

    public Matrix forward(Matrix xBatch) {
        Matrix result = xBatch.clone();
        for (Layer layer: layers)
            result = layer.forward(result);
        return result;
    }

    public Matrix backward(Matrix lossGradient) {
        Matrix result = lossGradient.clone();
        for (int i = 0; i < layers.size(); i++)
            result = layers.get(layers.size() - 1 - i).backward(result);
        return result;
    }

    public double calculateLoss(Matrix xBatch, Matrix yBatch) {
        Matrix prediction = forward(xBatch);
        return loss.forward(prediction, yBatch);
    }

    public double trainBatch(Matrix xBatch, Matrix yBatch) {
        Matrix predictions = forward(xBatch);
        double batchLoss = loss.forward(predictions, yBatch);
        Matrix lossGradient = loss.backward();
        backward(lossGradient);
        return batchLoss;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    @Override
    public Network clone() {
        try {
            Network clone = (Network) super.clone();
            clone.loss = loss.clone();
            clone.layers = layers.stream().map(Layer::clone).collect(Collectors.toList());
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network = (Network) o;
        return getLayers().equals(network.getLayers()) && loss.equals(network.loss);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLayers(), loss);
    }

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "layers=" + layers +
                ", loss=" + loss +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                "слои=" + layers.stream().map(layer -> layer.toString(debugMode)).collect(Collectors.toList()) +
                ", потеря=" + loss.toString(debugMode) +
                '}';
    }

    protected String getClassName() {
        return "Нейросеть";
    }

    protected String getDebugClassName() {
        return "Network";
    }
}
