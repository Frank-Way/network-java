import models.data.Dataset;
import models.data.approximation.ApproximationDataLoader;
import models.data.approximation.ApproximationLoadParameters;
import models.data.approximation.functions.Function;
import models.data.approximation.functions.examples.Sin2X;
import models.data.approximation.functions.examples.SinX;
import models.exceptions.SerializationException;
import models.math.Matrix;
import models.networks.Network;
import options.Constants;
import options.DefaultParameters;
import utils.Errors;
import utils.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Test {
    public static void main(String[] args) {
        String networksDirectory = "networks";
        Path pathToNetworks = Paths.get(networksDirectory);
        String networkFilename = "network_aa2333d5-912b-400f-b853-99d1c65be6b1_1658846250971.dat";
        Network network = null;
        try {
            network = (Network) Utils.load(pathToNetworks.toAbsolutePath().toString(), networkFilename);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
        if (network == null)
            System.exit(-1);

        Function function = new Sin2X(Sin2X.getDefaultVariableRanges());
        ApproximationLoadParameters parameters = new ApproximationLoadParameters(
                function,
                4096,
                DefaultParameters.TEST_PART,
                1.0,
                DefaultParameters.EXTENDING_FACTOR);
        Dataset dataset = new ApproximationDataLoader().load(parameters);

        Matrix x = dataset.getValidData().getInputs();
        Matrix t = dataset.getValidData().getOutputs();
        Matrix y = network.forward(x);
        Matrix e = t.sub(y).abs();
        Errors errors = Errors.buildFromTargetsAndPredictions(t, y);

        StringBuilder sb = new StringBuilder(String.format("Тестовый прогон загруженной нейросети " +
                "(путьКНейросети=\"%s\"; функция=\"%s\"; размерВыборки=%d",
                networkFilename, function, parameters.getSize()))
                .append("\n");

        sb.append(errors).append("\n");

        sb.append(Utils.networkParametersToString(network, Constants.DOUBLE_FORMAT)).append("\n");

        sb.append(Utils.networkIOToStringTable(x, t, y, e, 1.0, Constants.DOUBLE_FORMAT))
                .append("\n");

        System.out.println(sb);
    }
}
