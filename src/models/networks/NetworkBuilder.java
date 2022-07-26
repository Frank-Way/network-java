package models.networks;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.layers.DenseLayer;
import models.layers.Layer;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class NetworkBuilder implements Copyable<NetworkBuilder> {
    private final NetworkBuilderParameters builderParameters;

    public NetworkBuilder(@NotNull NetworkBuilderParameters builderParameters) {
        this.builderParameters = builderParameters;
    }

    public Network build() {
        return NetworkBuilder.build(builderParameters);
    }

    public static Network build(@NotNull NetworkBuilderParameters builderParameters) {
        List<Layer> layers = new ArrayList<>();
        for (int layer = 0; layer < builderParameters.getSizes().size() - 1; layer++)
            layers.add(new DenseLayer(builderParameters.getSize(layer),
                    builderParameters.getSize(layer + 1),
                    builderParameters.getActivation(layer).copy()));
        return new Network(layers, builderParameters.getLoss().copy());
    }

    @Override
    public NetworkBuilder copy() {
        return new NetworkBuilder(Utils.copyNullable(builderParameters));
    }
}
