package utils.automatization.mutators;

import models.data.approximation.ApproxLoadParameters;
import models.data.approximation.ApproxLoadParametersBuilder;
import models.data.approximation.NoiseMode;
import models.trainers.FitParametersBuilder;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;

import java.util.stream.IntStream;

@YamlSerializable
public class ApproxLoadParametersMutator {
    @YamlField private final ApproxLoadParametersBuilder approxLoadParametersBuilder;
    @YamlField private double[] sizesFactors;
    @YamlField private double[] extendingFactorsBiases;
    @YamlField private NoiseMode[] noiseModes;

    public ApproxLoadParametersMutator(ApproxLoadParameters approxLoadParameters) {
        this.approxLoadParametersBuilder = approxLoadParameters.builder();
    }

    public ApproxLoadParametersMutator(ApproxLoadParametersBuilder approxLoadParametersBuilder) {
        this.approxLoadParametersBuilder = approxLoadParametersBuilder;
    }

    public ApproxLoadParametersMutator mutateSizes(double ... factors) {
        sizesFactors = factors;
        return this;
    }

    public ApproxLoadParametersMutator mutateExtendingFactors(double ... biases) {
        extendingFactorsBiases = biases;
        return this;
    }

    public ApproxLoadParametersMutator mutateNoiseModes(NoiseMode ... noiseModes) {
        this.noiseModes = noiseModes;
        return this;
    }

    public ApproxLoadParametersBuilder[] mutate() {
        final ApproxLoadParametersBuilder[] mutatedBySizes = mutateSizesImpl();
        final ApproxLoadParametersBuilder[] mutatedByExtendingFactors = mutateExtendingFactorsImpl();
        final ApproxLoadParametersBuilder[] mutatedByNoiseModes = mutateNoiseModesImpl();
        final ApproxLoadParametersBuilder[] result;
        result = new ApproxLoadParametersBuilder[mutatedBySizes.length * mutatedByExtendingFactors.length];

        int cnt = 0;
        for (ApproxLoadParametersBuilder mutatedBySize : mutatedBySizes)
            for (ApproxLoadParametersBuilder mutatedByExtendingFactor : mutatedByExtendingFactors)
                for (ApproxLoadParametersBuilder mutatedByNoiseMode : mutatedByNoiseModes)
                    result[cnt++] = squashByNoiseModes(
                            squashByExtendingFactors(mutatedBySize, mutatedByExtendingFactor),
                            mutatedByNoiseMode);
        return result;
    }

    private ApproxLoadParametersBuilder[] mutateSizesImpl() {
        if (sizesFactors == null)
            return new ApproxLoadParametersBuilder[] {approxLoadParametersBuilder.deepCopy()};

        final int totalCount = 2 * sizesFactors.length + 1;

        final ApproxLoadParametersBuilder[] result = IntStream.range(0, totalCount)
                .mapToObj(i -> approxLoadParametersBuilder.deepCopy())
                .toArray(ApproxLoadParametersBuilder[]::new);

        final int[] sizes = approxLoadParametersBuilder.getSizes();
        final int size = approxLoadParametersBuilder.getSize();

        if (sizes != null) {
            final int[][] mutatedSizes = new int[sizes.length][totalCount];

            for (int i = 0; i < sizes.length; i++) {
                IntegerMutator integerMutator = new IntegerMutator(sizes[i]);
                mutatedSizes[i] = integerMutator.mutateMultiply(sizesFactors);
            }

            final int[][] transposedMutatedSizes = new int[totalCount][sizes.length];

            for (int i = 0; i < sizes.length; i++)
                for (int j = 0; j < totalCount; j++)
                    transposedMutatedSizes[j][i] = mutatedSizes[i][j];

            for (int j = 0; j < totalCount; j++) {
                result[j].sizes(transposedMutatedSizes[j]);
            }

            return result;
        }

        if (size < 1)
            throw new IllegalStateException("Не корректный размер выборки: " + size);

        final IntegerMutator integerMutator = new IntegerMutator(size);
        final int[] mutatedSize = integerMutator.mutateMultiply(sizesFactors.length);

        for (int j = 0; j < totalCount; j++)
            result[j].size(mutatedSize[j]);

        return result;
    }

    private ApproxLoadParametersBuilder[] mutateExtendingFactorsImpl() {
        if (extendingFactorsBiases == null)
            return new ApproxLoadParametersBuilder[] {approxLoadParametersBuilder.deepCopy()};
        final int totalCount = 2 * extendingFactorsBiases.length + 1;

        final ApproxLoadParametersBuilder[] result = IntStream.range(0, totalCount)
                .mapToObj(i -> approxLoadParametersBuilder.deepCopy())
                .toArray(ApproxLoadParametersBuilder[]::new);

        final double[] extendingFactors = approxLoadParametersBuilder.getExtendingFactors();
        final double extendingFactor = approxLoadParametersBuilder.getExtendingFactor();

        if (extendingFactors != null) {
            final double[][] mutatedExtendingFactors = new double[extendingFactors.length][totalCount];

            for (int i = 0; i < extendingFactors.length; i++) {
                DoubleMutator doubleMutator = new DoubleMutator(extendingFactors[i]);
                mutatedExtendingFactors[i] = doubleMutator.mutateAdd(extendingFactorsBiases);
            }

            final double[][] transposedMutatedExtendingFactors = new double[totalCount][extendingFactors.length];

            for (int i = 0; i < extendingFactors.length; i++)
                for (int j = 0; j < totalCount; j++)
                    transposedMutatedExtendingFactors[j][i] = mutatedExtendingFactors[i][j];

            for (int j = 0; j < totalCount; j++) {
                result[j].extendingFactors(transposedMutatedExtendingFactors[j]);
            }

            return result;
        }

        if (extendingFactor < 1e-9)
            throw new IllegalStateException("Не корректный множитель расширения выборки: " + extendingFactor);

        final DoubleMutator doubleMutator = new DoubleMutator(extendingFactor);
        final double[] mutatedExtendingFactor = doubleMutator.mutateAdd(extendingFactorsBiases);

        for (int j = 0; j < totalCount; j++)
            result[j].extendingFactor(mutatedExtendingFactor[j]);

        return result;
    }

    private ApproxLoadParametersBuilder[] mutateNoiseModesImpl() {
        if (noiseModes == null)
            return new ApproxLoadParametersBuilder[] {approxLoadParametersBuilder.deepCopy()};

        final int totalCount = noiseModes.length + 1;

        final ApproxLoadParametersBuilder[] result = IntStream.range(0, totalCount)
                .mapToObj(i -> approxLoadParametersBuilder.deepCopy())
                .toArray(ApproxLoadParametersBuilder[]::new);

        for (int i = 0; i < noiseModes.length; i++)
            result[i].noiseMode(noiseModes[i]);

        return result;
    }

    private static ApproxLoadParametersBuilder squashBySizes(ApproxLoadParametersBuilder builder1,
                                                             ApproxLoadParametersBuilder builder2) {
        return builder1.deepCopy().sizes(builder2.getSizes());
    }

    private static ApproxLoadParametersBuilder squashByExtendingFactors(ApproxLoadParametersBuilder builder1,
                                                                        ApproxLoadParametersBuilder builder2) {
        return builder1.deepCopy().extendingFactors(builder2.getExtendingFactors());
    }

    private static ApproxLoadParametersBuilder squashByNoiseModes(ApproxLoadParametersBuilder builder1,
                                                                  ApproxLoadParametersBuilder builder2) {
        return builder1.deepCopy().noiseMode(builder2.getNoiseMode());
    }
}
