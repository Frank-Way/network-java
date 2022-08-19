import com.sun.xml.internal.ws.encoding.soap.SerializationException;
import models.data.Dataset;
import models.data.approximation.ApproximationDataLoader;
import models.data.approximation.ApproximationLoadParameters;
import models.data.approximation.functions.Function;
import models.data.approximation.functions.examples.SinX1_mul_X2;
import models.math.Matrix;
import models.networks.Network;
import options.DefaultParameters;
import serialization.SerializationType;
import serialization.SerializationUtils;
import utils.Errors;
import utils.Utils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Класс для тестирования сериализованной сети
 */
public class Test {
    public static void main(String[] args) {
//        /*
        String doubleFormat = "%15.10f";
        String networksDirectory = "networks";  // путь к директории с сетями
        Path pathToNetworks = Paths.get(networksDirectory);

        // имя файла с сетью (без расширения!!!)
        String networkFilename = "";
        Network network = null;

        try {
            network = SerializationUtils.load(pathToNetworks.toAbsolutePath().toString(), networkFilename,
                    SerializationType.YAML);  // десериализация
        } catch (SerializationException e) {
            e.printStackTrace();
        }

        if (network == null)
            System.exit(-1);

        Function function = new SinX1_mul_X2(SinX1_mul_X2.getDefaultVariableRanges());  // функция, на которую была обучена сеть
        ApproximationLoadParameters parameters = new ApproximationLoadParameters(
                function,
                64,  // размер выборки
                DefaultParameters.TEST_PART,  // не имеет значения
                1.0,  // 1.0, чтобы для валидации использовалась вся выборка
                DefaultParameters.EXTENDING_FACTOR);  // не имеет значения
        Dataset dataset = new ApproximationDataLoader().load(parameters);  // обучающая выборка

        Matrix x = dataset.getValidData().getInputs();  // входные значения выборки
        Matrix t = dataset.getValidData().getOutputs();  // требуемые выходные значения выборки
        Matrix y = network.forward(x);  // выход сети
        Matrix e = t.sub(y).abs();  // ошибки вычисления сети
        Errors errors = Errors.buildFromTargetsAndPredictions(t, y);  // максимальные ошибки

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
//        */
    }
}
