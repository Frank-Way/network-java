package tests.cases.serialization;

import models.data.approximation.ApproxDataLoader;
import models.data.approximation.ApproxLoadParameters;
import models.data.approximation.functions.impl.SinX;
import models.layers.DenseLayer;
import models.losses.MeanSquaredError;
import models.math.Matrix;
import models.networks.Network;
import models.networks.NetworkBuilder;
import models.operations.*;
import models.optimizers.SGD;
import models.trainers.FitParameters;
import serialization.serializers.Serializer;
import tests.TestStatus;
import tests.entities.serialization.*;
import tests.utils.ValuesProvider;
import utils.automatization.ExperimentBuilder;
import utils.automatization.RunConfiguration;
import utils.automatization.RunConfigurationBuilder;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ModelClassesSerialization extends BaseForSerialization {
    @Override
    public String getDescription() {
        return "Проверка сериализации классов: [" + convertObjectsToJoinedClassNames(getTestObjects())
                + " ]";
    }

    @Override
    public TestStatus process(Object... args) {
        final ValuesProvider valuesProvider = (ValuesProvider) args[0];
        final Serializer serializer = (Serializer) args[1];
        final boolean isDeserializationRequired = (boolean) args[2];

        final Object[] testObjects = getTestObjects();
        for (Object testObject : testObjects) {
            final TestStatus testStatus = innerTest(testObject, isDeserializationRequired, serializer);
            if (testStatus != TestStatus.PASSED)
                return testStatus;
        }
        return TestStatus.PASSED;
    }

    private Object[] getTestObjects() {
        final Matrix testMatrix1 = new Matrix(new double[][] {
                { 1.1, -2.2,  3.3},
                { 4.4,  5.5,  6.6},
                {-7.7, -8.8, -9.9}
        });
        final NetworkBuilder testNetworkBuilder1 = Network.newAnotherBuilder()
                .loss(new MeanSquaredError())
                .activations(new TanhActivation(), new LinearActivation())
                .sizes(1, 8, 1);
        final RunConfigurationBuilder testRunConfigurationBuilder1 = new RunConfigurationBuilder()
                .retries(3)
                .description("Тестовая конфигурация")
                .fitParametersBuilder(FitParameters.newBuilder()
                        .epochs(1000)
                        .networkBuilder(testNetworkBuilder1.deepCopy())
                        .optimizerBuilder(SGD.newBuilder().startLR(0.1).stopLR(0.0001))
                        .dataLoader(new ApproxDataLoader())
                        .loadParameters(ApproxLoadParameters.newBuilder()
                                .function(new SinX())
                                .size(100)
                                .build()));

        final ExperimentBuilder testExperimentBuilder1 = new ExperimentBuilder()
                .description("Тестовый эксперимент")
                .runConfigurationBuilders(testRunConfigurationBuilder1,
                        testRunConfigurationBuilder1.description("Тестовая конфигурация 2"));
        return new Object[]{
                testMatrix1.deepCopy(),
                new TanhActivation(),
                new WeightMultiply(testMatrix1.deepCopy()),
                new DenseLayer(3, new Operation[] {
                        new WeightMultiply(testMatrix1.getRow(0)),
                        new BiasAdd(testMatrix1.getCol(0)),
                        new TanhActivation()
                }),
                testNetworkBuilder1.deepCopy(),
                testNetworkBuilder1.deepCopy().build(),
                testRunConfigurationBuilder1.deepCopy(),
                testRunConfigurationBuilder1.deepCopy().build(),
                testExperimentBuilder1.deepCopy(),
                testExperimentBuilder1.deepCopy().build()
        };
    }

}