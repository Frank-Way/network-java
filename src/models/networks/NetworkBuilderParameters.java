package models.networks;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.losses.Loss;
import models.operations.Operation;
import utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Параметры для построения сети. Атрибуты модели:
 *  sizes - размеры слоёв сети, от входного до выходного. Первый элемент - количество входов сети, последний -
 *          количество выходов, остальные - размеры скрытых слоёв;
 *  список<{@link Operation}> - набор активаций для каждого слоя, не считая входного. То есть количество операций
 *                              равняется размерности sizes минус один;
 *  {@link Loss} - потеря для оценки сети
 */
public class NetworkBuilderParameters implements Copyable<NetworkBuilderParameters> {
    private final List<Integer> sizes;
    private final List<Operation> activations;
    private final Loss loss;

    /**
     * Конструктор. При несовпадении размеров будет выброшено IllegalArgumentException
     * @param sizes размеры слоёв
     * @param activations функции активации
     * @param loss потеря
     */
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
