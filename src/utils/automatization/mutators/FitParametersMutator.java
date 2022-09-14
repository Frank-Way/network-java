package utils.automatization.mutators;

import models.data.LoadParameters;
import models.data.approximation.ApproxLoadParameters;
import models.data.approximation.ApproxLoadParametersBuilder;
import models.trainers.FitParameters;
import models.trainers.FitParametersBuilder;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;

import java.util.stream.IntStream;

@YamlSerializable
public class FitParametersMutator {
    @YamlField private final FitParametersBuilder fitParametersBuilder;
    @YamlField private double[] epochsFactors;
    @YamlField private double[] sizesFactors;
    @YamlField private double[] extendingFactorsBiases;

    public FitParametersMutator(FitParameters fitParameters) {
        this.fitParametersBuilder = fitParameters.builder();
    }

    public FitParametersMutator(FitParametersBuilder fitParametersBuilder) {
        this.fitParametersBuilder = fitParametersBuilder;
    }

    public FitParametersBuilder[] mutate() {
        LoadParameters loadParameters = fitParametersBuilder.getLoadParameters();
        if (!(loadParameters instanceof ApproxLoadParameters)) {
            return new FitParametersBuilder[] {fitParametersBuilder.deepCopy()};
        }
        ApproxLoadParameters approxLoadParameters = (ApproxLoadParameters) loadParameters;

        final FitParametersBuilder[] mutatedByEpochs = mutateEpochsImpl();
        final FitParametersBuilder[] mutatedByLoadParameters = mutateLoadParametersImpl();
        final FitParametersBuilder[] result;
        result = new FitParametersBuilder[mutatedByEpochs.length * mutatedByLoadParameters.length];

        int cnt = 0;
        for (FitParametersBuilder mutatedByEpoch : mutatedByEpochs)
            for (FitParametersBuilder mutatedByLoadParameter : mutatedByLoadParameters)
                result[cnt++] = squashByLoadParameters(mutatedByEpoch, mutatedByLoadParameter);
        return result;
    }

    public FitParametersMutator mutateEpochs(double ... factors) {
        epochsFactors = factors;
        return this;
    }

    public FitParametersMutator mutateSizes(double ... factors) {
        sizesFactors = factors;
        return this;
    }

    public FitParametersMutator mutateExtendingFactors(double ... biases) {
        extendingFactorsBiases = biases;
        return this;
    }

    private FitParametersBuilder[] mutateEpochsImpl() {
        if (epochsFactors == null)
            return new FitParametersBuilder[] {fitParametersBuilder.deepCopy()};

        final int totalCount = 2 * epochsFactors.length + 1;
        final FitParametersBuilder[] result = IntStream.range(0, totalCount)
                .mapToObj(i -> fitParametersBuilder.deepCopy())
                .toArray(FitParametersBuilder[]::new);
        final IntegerMutator integerMutator = new IntegerMutator(fitParametersBuilder.getEpochs());
        final int[] mutatedEpochs = integerMutator.mutateMultiply(epochsFactors);
        for (int i = 0; i < totalCount; i++)
            result[i].epochs(mutatedEpochs[i]);
        return result;
    }

    private FitParametersBuilder[] mutateLoadParametersImpl() {
        LoadParameters loadParameters = fitParametersBuilder.getLoadParameters();
        ApproxLoadParameters approxLoadParameters = (ApproxLoadParameters) loadParameters;
        final ApproxLoadParametersMutator approxLoadParametersMutator;
        approxLoadParametersMutator = new ApproxLoadParametersMutator(approxLoadParameters)
                .mutateSizes(sizesFactors)
                .mutateExtendingFactors(extendingFactorsBiases);
        final ApproxLoadParametersBuilder[] mutatedApproxLoadParameters = approxLoadParametersMutator.mutate();

        final FitParametersBuilder[] result = IntStream.range(0, mutatedApproxLoadParameters.length)
                .mapToObj(i -> fitParametersBuilder.deepCopy())
                .toArray(FitParametersBuilder[]::new);
        for (int i = 0; i < mutatedApproxLoadParameters.length; i++)
            result[i].loadParameters(mutatedApproxLoadParameters[i].build());
        return result;
    }

    private static FitParametersBuilder squashByEpochs(FitParametersBuilder builder1,
                                                       FitParametersBuilder builder2) {
        return builder1.deepCopy().epochs(builder2.getEpochs());
    }

    private static FitParametersBuilder squashByLoadParameters(FitParametersBuilder builder1,
                                                               FitParametersBuilder builder2) {
        return builder1.deepCopy().loadParameters(builder2.getLoadParameters());
    }
}
