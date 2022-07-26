package models.networks;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.losses.Loss;
import models.operations.Operation;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NetworkBuilderParameters implements Copyable<NetworkBuilderParameters> {
    private final List<Integer> sizes;
    private final List<Operation> activations;
    private final Loss loss;

    public NetworkBuilderParameters(@NotNull List<Integer> sizes, @NotNull List<Operation> activations, @NotNull Loss loss) {
        if (sizes.size() != (1 + activations.size()) || activations.size() < 2)
            throw new IllegalArgumentException(
                    String.format("Размеры sizes и activations должны быть равны и больше 1 (sizes=%d, activations=%d)",
                            sizes.size(), activations.size()));
        this.sizes = sizes;
        this.activations = activations;
        this.loss = loss;
    }

    public List<Integer> getSizes() {
        return sizes;
    }

    public Integer getSize(int layer) {
        return sizes.get(layer);
    }

    public List<Operation> getActivations() {
        return activations;
    }

    public Operation getActivation(int layer) {
        return activations.get(layer);
    }

    public Loss getLoss() {
        return loss;
    }

    @Override
    public NetworkBuilderParameters copy() {
        return new NetworkBuilderParameters(
                new ArrayList<>(sizes),
                activations.stream().map(Utils::copyNullable).collect(Collectors.toList()),
                Utils.copyNullable(loss));
    }
}
