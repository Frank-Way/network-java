package models.optimizers;

import models.networks.Network;
import utils.copy.CopyUtils;

public class SGDBuilder extends OptimizerBuilder {
    public SGDBuilder() {
    }

    protected SGDBuilder(Network network, double learningRate, double decayLR, double startLR, double stopLR, int epochs) {
        super(network, learningRate, decayLR, startLR, stopLR, epochs);
    }

    @Override
    public Optimizer build() {
        validate();
        calculateDecayLR();
        return new SGD(network, learningRate, decayLR);
    }

    @Override
    public SGDBuilder deepCopy() {
        return new SGDBuilder(network == null ? null : network.deepCopy(), learningRate, decayLR, startLR, stopLR, epochs);
    }
}
