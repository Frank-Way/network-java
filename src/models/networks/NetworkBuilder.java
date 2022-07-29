package models.networks;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.layers.DenseLayer;
import models.layers.Layer;

import java.util.ArrayList;
import java.util.List;

/**
 * Генератор/билдер сетей
 */
public abstract class NetworkBuilder implements Copyable<NetworkBuilder> {
    private NetworkBuilder() {}

    /**
     * Генерация сети с заданными параметрами, см. {@link NetworkBuilderParameters}
     * @param builderParameters параметры сети
     * @return сеть
     */
    public static Network build(@NotNull NetworkBuilderParameters builderParameters) {
        List<Layer> layers = new ArrayList<>();
        for (int layer = 0; layer < builderParameters.getSizes().size() - 1; layer++)  // сеть строится послойно
            layers.add(new DenseLayer(builderParameters.getSize(layer),  // по заданным размерностям текущего
                    builderParameters.getSize(layer + 1),  // и следующего слоёв
                    builderParameters.getActivation(layer).copy()));  // с копией функции активации
        return new Network(layers, builderParameters.getLoss().copy());  // в сеть попадает копия потери
    }
}
