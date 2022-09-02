import models.data.Dataset;
import models.data.approximation.ApproximationDataLoader;
import models.data.approximation.ApproximationLoadParameters;
import models.data.approximation.functions.Function;
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

/**
 * Класс для тестирования сериализованной сети
 */
public class RunSerializedNetwork {
    public static void main(String[] args) {
        String doubleFormat = "%15.10f";
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

        Function function = new SinX1_mul_X2(SinX1_mul_X2.getDefaultVariableRanges());  // функция, на которую была обучена сеть
        ApproximationLoadParameters parameters = ApproximationLoadParameters.newBuilder()
                .function(function)
                .size(64)  // размер выборки
                .testSize(64)  // не имеет значения
                .validSize(64)  // 1.0, чтобы для валидации использовалась вся выборка
                .build();
        Dataset dataset = new ApproximationDataLoader().load(parameters);  // обучающая выборка

        Matrix x = dataset.getValidData().getInputs();  // входные значения выборки
        Matrix t = dataset.getValidData().getOutputs();  // требуемые выходные значения выборки
        Matrix y = network.forward(x);  // выход сети
        Matrix e = t.sub(y).abs();  // ошибки вычисления сети
        Errors errors = new Errors(t, y);  //  ошибки

        // формирование отчёта
        StringBuilder sb = new StringBuilder(String.format("Тестовый прогон загруженной нейросети " +
                        "(путьКНейросети=\"%s\"; функция=\"%s\"; размерВыборки=%d",
                networkFilename, function, parameters.getSize()))
                .append("\n");  // шапка

        sb.append(errors).append("\n");  // максимальные ошибки

        sb.append(Utils.networkParametersToString(network, doubleFormat)).append("\n");  // параметры сети

        sb.append(Utils.networkIOToStringTable(x, t, y, e, 1.0, doubleFormat))
                .append("\n");  // таблица по всей выборке

        System.out.println(sb);
    }
}