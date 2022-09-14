package utils.automatization.mutators;

import java.util.Arrays;

public class IntegerMutator {
    private final int value;

    public IntegerMutator(int value) {
        this.value = value;
    }

    public int[] mutateMultiply(int ... factors) {
        return mutateMultiply(Arrays.stream(factors).mapToDouble(i -> i * 1.0).toArray());
    }

    public int[] mutateMultiply(double ... factors) {
        final int[] result = new int[2 * factors.length + 1];
        final int totalCount = result.length;

        int cnt = 0;

        for (double factor: factors) {
            result[cnt] = (int) (value / factor);
            result[totalCount - cnt - 1] = (int) (value * factor);
            cnt++;
        }
        result[cnt] = value;

        return result;
    }



    public int[] mutateAdd(int ... biases) {
        final int[] result = new int[2 * biases.length + 1];
        final int totalCount = result.length;

        int cnt = 0;

        for (int bias: biases) {
            result[cnt] = value - bias;
            result[totalCount - cnt - 1] = value + bias;
            cnt++;
        }
        result[cnt] = value;

        return result;
    }
}
