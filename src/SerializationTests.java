import models.layers.DenseLayer;
import models.layers.Layer;
import models.losses.Loss;
import models.losses.MeanSquaredError;
import models.networks.Network;
import models.operations.*;
import serialization.YamlSerializationUtils;
import serialization.formatters.Formatter;
import serialization.formatters.yaml.YamlFormatter;
import serialization.wrappers.ArrayWrapper;
import serialization.wrappers.ObjectWrapper;
import utils.automatization.ExperimentConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Objects;

public class SerializationTests {
    final static String DOUBLE_FORMAT = "%10.8f";
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        boolean deserialize = true;

        try {
            testNetwork(deserialize);
//            testExperimentConfiguration(deserialize);
            testExperimentConfigurations(deserialize);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    static void testNetwork(boolean deserialize) throws IllegalAccessException {
        System.out.println("TEST NETWORK");
        Loss loss1 = new MeanSquaredError();
        Operation operation1 = new TanhActivation();
        Layer layer1 = new DenseLayer(1, 3, operation1);
        Operation operation2 = new LinearActivation();
        Layer layer2 = new DenseLayer(3, 1, operation2);

        Formatter formatter = new YamlFormatter(DOUBLE_FORMAT);

        Network network1 = new Network(new Layer[]{layer1, layer2}, loss1);
        ObjectWrapper wrapper_network1 = new ObjectWrapper(network1.getClass(), formatter);
        String network1S = wrapper_network1.writeValue(network1);
        System.out.println("serialized network1");
        System.out.println(network1S);
        System.out.println();

        if (deserialize) {
            ObjectWrapper wrapper_network1D = new ObjectWrapper(Network.class, formatter);
            Network network1D = (Network) wrapper_network1D.readValue(network1S);
            assertEquals(network1, network1D);
        }
    }

    static void testExperimentConfiguration(boolean deserialize) throws IllegalAccessException {
        System.out.println("TEST EXPERIMENT CONFIGURATION");

        ExperimentConfiguration[] experimentConfigurations = ExperimentConfigurations.getDefaultExperimentConfigurations();
        ExperimentConfiguration experimentConfiguration = experimentConfigurations[0];

        Formatter formatter = new YamlFormatter(DOUBLE_FORMAT);

        ObjectWrapper wrapper_experimentConfiguration = new ObjectWrapper(experimentConfiguration.getClass(), formatter);
        String experimentConfigurationS = wrapper_experimentConfiguration.writeValue(experimentConfiguration);
        System.out.println("serialized experimentConfiguration");
        System.out.println(experimentConfigurationS);
        System.out.println();

        if (deserialize) {
            ObjectWrapper wrapper_experimentConfigurationD = new ObjectWrapper(ExperimentConfiguration.class, formatter);
            ExperimentConfiguration experimentConfigurationD = (ExperimentConfiguration) wrapper_experimentConfigurationD.readValue(experimentConfigurationS);
            assertEquals(experimentConfiguration, experimentConfigurationD);
        }
    }

    static void testExperimentConfigurations(boolean deserialize) throws IllegalAccessException {
        System.out.println("TEST EXPERIMENT CONFIGURATIONS");

        ExperimentConfiguration[] experimentConfigurations = ExperimentConfigurations.getDefaultExperimentConfigurations();

        Formatter formatter = new YamlFormatter(DOUBLE_FORMAT);

        ArrayWrapper wrapper_experimentConfigurations = new ArrayWrapper(experimentConfigurations.getClass(), formatter);
        String experimentConfigurationsS = wrapper_experimentConfigurations.writeValue(experimentConfigurations);
        System.out.println("serialized experimentConfigurationsS");
        System.out.println(experimentConfigurationsS);
        System.out.println();

        if (deserialize) {
            ArrayWrapper wrapper_experimentConfigurationsD = new ArrayWrapper(ExperimentConfiguration[].class, formatter);
            ExperimentConfiguration[] experimentConfigurationsD = Arrays.stream((Object[]) wrapper_experimentConfigurationsD.readValue(experimentConfigurationsS)).map(ExperimentConfiguration.class::cast).toArray(ExperimentConfiguration[]::new);
            assertEquals(experimentConfigurations, experimentConfigurationsD);
        }
    }

    static <T> void assertEquals(T o1, T o2) {
        if (o1.getClass().isArray()) {
            if (!YamlSerializationUtils.yamlFieldsDeepEquals(o1, o2))
                throw new AssertionError("Объекты не равны:\n" + o1 + "\n\n" + o2);
            else if (!Arrays.deepEquals((Object[]) o1, (Object[]) o2))
                System.out.println("Не-сериализуемые поля не совпали:\n" + o1 + "\n\n" + o2);
        } else if (!YamlSerializationUtils.yamlFieldsEquals(o1, o2))
            throw new AssertionError("Объекты не равны:\n" + o1 + "\n\n" + o2);
        else if (!Objects.equals(o1, o2))
            System.out.println("Не-сериализуемые поля не совпали:\n" + o1 + "\n\n" + o2);
        System.out.println("Объекты равны:\n" + o1 + "\n\n" + o2);
    }
}
