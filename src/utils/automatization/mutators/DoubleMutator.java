package utils.automatization.mutators;

import java.util.Arrays;

public class DoubleMutator {
    private final double value;

    public DoubleMutator(double value) {
        this.value = value;
    }

    public double[] mutateMultiply(int ... factors) {
        return mutateMultiply(Arrays.stream(factors).mapToDouble(i -> i * 1.0).toArray());
    }

    public double[] mutateMultiply(double ... factors) {
        final double[] result = new double[2 * factors.length + 1];
        final int totalCount = result.length;

        int cnt = 0;

        for (double factor: factors) {
            result[cnt] = value / factor;
            result[totalCount - cnt - 1] = value * factor;
            cnt++;
        }
        result[cnt] = value;

        return result;
    }

    public double[] mutateAdd(double ... biases) {
        final double[] result = new double[2 * biases.length + 1];
        final int totalCount = result.length;

        int cnt = 0;

        for (double bias: biases) {
            result[cnt] = value - bias;
            result[totalCount - cnt - 1] = value + bias;
            cnt++;
        }
        result[cnt] = value;

        return result;
    }
}

