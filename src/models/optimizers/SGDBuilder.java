package models.optimizers;

import models.networks.Network;

/**
 * Билдер для {@link SGD}, наследник {@link OptimizerBuilder}
 */
public class SGDBuilder extends OptimizerBuilder {
    public SGDBuilder() {
    }

    protected SGDBuilder(Network network, double learningRate, double decayLR, double startLR, double stopLR, int epochs) {
        super(network, learningRate, decayLR, startLR, stopLR, epochs);
    }

    @Override
    public SGD build() {
        validate();
        validate();
        return new SGD(network, learningRate, decayLR);
    }

    @Override
    public SGDBuilder deepCopy() {
        return new SGDBuilder(network == null ? null : network.deepCopy(), learningRate, decayLR, startLR, stopLR, epochs);
    }
}
