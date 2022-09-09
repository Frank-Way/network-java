import models.data.Dataset;
import models.data.approximation.ApproxDataLoader;
import models.data.approximation.ApproxLoadParameters;
import models.data.approximation.functions.Function;
import models.data.approximation.functions.impl.Sin2X;
import models.data.approximation.functions.impl.SinX1_mul_X2;
import models.math.Matrix;
import models.networks.Network;
import serialization.SerializationType;
import serialization.SerializationUtils;
import serialization.exceptions.SerializationException;
import utils.Errors;
import utils.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Класс для тестирования сериализованной сети
 */
public class RunSerializedNetwork {
    public static void main(String[] args) {
        String doubleFormat = "%25.20f";
        String networksDirectory = "networks";  // путь к директории с сетями
        Path pathToNetworks = Paths.get(networksDirectory);

        // имя файла с сетью (без расширения!!!)
        String networkFilename = "";
        Network network = null;

        try {
            network = (Network) SerializationUtils.load(Network.class,
                    pathToNetworks.toAbsolutePath().toString(), networkFilename,
                    SerializationType.YAML);  // десериализация
        } catch (SerializationException e) {
            e.printStackTrace();
            return;
        }

        if (network == null)
            System.exit(-1);

        Function function = new Sin2X();  // функция, на которую была обучена сеть
        ApproxLoadParameters parameters = ApproxLoadParameters.newBuilder()
                .function(function)
                .size(100)
                .testSize(100)
                .validSize(100)
                .build();
        Dataset dataset = new ApproxDataLoader().load(parameters);  // обучающая выборка

        Matrix x = dataset.getValidData().getInputs();  // входные значения выборки
        Matrix t = dataset.getValidData().getOutputs();  // требуемые выходные значения выборки
        Matrix y = network.forward(x);  // выход сети
        Matrix e = t.sub(y).abs();  // ошибки вычисления сети
        Errors errors = new Errors(t, y);  //  ошибки

        // формирование отчёта
        StringBuilder sb = new StringBuilder(String.format("Тестовый прогон загруженной нейросети " +
                        "(путьКНейросети=\"%s\"; функция=\"%s\"; размерВыборки=%s",
                networkFilename, function, Arrays.toString(parameters.getSizes())))
                .append("\n");  // шапка

        sb.append(errors).append("\n");  // максимальные ошибки

        sb.append(Utils.networkParametersToString(network, doubleFormat)).append("\n");  // параметры сети

        sb.append(Utils.networkIOToStringTable(x, t, y, e, 1.0, doubleFormat))
                .append("\n");  // таблица по всей выборке

        System.out.println(sb);
    }
}
