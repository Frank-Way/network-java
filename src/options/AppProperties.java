package options;

import serialization.SerializationType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Оболочка над файлом настроек "app.properties". Список настроек:
 * <pre><ul>
 *     <li>debugMode                     - включен ли режим отладки; при true выводится больше информации;</li>
 *     <li>doubleFormat                  - формат вывода дробных чисел (до точки - общее количество цифр, после -
 *                                         количество цифр дробной части);</li>
 *     <li>printRequired                 - нужно ли выводить какую-либо информацию, флаг объединяется с другими
 *                                         настройками вывода по И, так что можно в одном месте "выключить" весь
 *                                         вывод, не меняя остальные настройки вывода;</li>
 *     <li>printConfigurationEach        - вывод по каждой попытке обучения;</li>
 *     <li>printConfigurationBest        - вывод по наилучшей попытке обучения для каждой конфигурации;</li>
 *     <li>printExperimentEach           - вывод по наилучшей попытке обучения для всех конфигураций каждого эксперимента;</li>
 *     <li>printExperimentBest           - вывод по наилучшей попытке обучения для всех экспериментов;</li>
 *     <li>saveRequired                  - флаг, аналогичный printRequired, но для сохранения обученных сетей;</li>
 *     <li>savePath                      - папка для сохранения обученных сетей;</li>
 *     <li>saveFilenamePattern           - шаблон для имени файлов с сетями;</li>
 *     <li>saveConfigurationEach         - сохранение сети по попытке обучения;</li>
 *     <li>saveConfigurationBest         - сохранение сети по наилучшей попытке обучения для каждой конфигурации;</li>
 *     <li>saveExperimentEach            - сохранение сети по наилучшей попытке обучения для всех конфигураций каждого эксперимента;</li>
 *     <li>saveExperimentBest            - сохранение сети по наилучшей попытке обучения для всех экспериментов;</li>
 *     <li>saveSerializationType         - тип сериализации;</li>
 *     <li>threadPoolSize                - максимальный размер пула тредов;</li>
 *     <li>experimentsSourceType         - откуда считывать описание экспериментов;</li>
 *     <li>experimentsSourceYamlPath     - папка с YAML-файлом с описанием экспериментов;</li>
 *     <li>experimentsSourceYamlFilename - имя YAML-файла с описанием экспериментов;</li>
 * </ul></pre>
 */
public class AppProperties {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final static String userDir = System.getProperty("user.dir");
    private final static String propertiesFileName = userDir + File.separator + "src" +
            File.separator + "app.properties";

    private final boolean debugMode;
    private final String doubleFormat;
    private final boolean printRequired;

    private final PrintOptions printConfigurationEach;
    private final PrintOptions printConfigurationBest;
    private final PrintOptions printExperimentEach;
    private final PrintOptions printExperimentBest;

    private final String savePath;
    private final String saveFilenamePattern;
    private final SerializationType saveSerializationType;
    private final boolean saveRequired;

    private final boolean saveConfigurationEach;
    private final boolean saveConfigurationBest;
    private final boolean saveExperimentEach;
    private final boolean saveExperimentBest;

    private final int threadPoolSize;

    private final ExperimentsSourceType experimentsSourceType;
    private final String experimentsSourceYamlPath;
    private final String experimentsSourceYamlFilename;

    public AppProperties() throws IOException {
        this(propertiesFileName);
    }

    public AppProperties(String propertiesFileName) throws IOException {
        try (InputStream input = new FileInputStream(propertiesFileName)) {

            Properties properties = new Properties();
            properties.load(input);

            debugMode = Boolean.parseBoolean(properties.getProperty("debug.mode", "false"));
            doubleFormat = properties.getProperty("double.format", "%15.10f");
            printRequired = Boolean.parseBoolean(properties.getProperty("print.required", "false"));
            printConfigurationEach = readPrintOptions(properties, "configuration.each");
            printConfigurationBest = readPrintOptions(properties, "configuration.best");
            printExperimentEach = readPrintOptions(properties, "experiment.each");
            printExperimentBest = readPrintOptions(properties, "experiment.best");
            saveRequired = Boolean.parseBoolean(properties.getProperty("save.required", "false"));
            savePath = userDir + File.separator + properties.getProperty("save.path", "networks");
            saveFilenamePattern = properties.getProperty("save.filename.pattern", "network_%s");
            saveConfigurationEach = Boolean.parseBoolean(properties.getProperty("save.configuration.each", "false"));
            saveConfigurationBest = Boolean.parseBoolean(properties.getProperty("save.configuration.best", "false"));
            saveExperimentEach = Boolean.parseBoolean(properties.getProperty("save.experiment.each", "false"));
            saveExperimentBest = Boolean.parseBoolean(properties.getProperty("save.experiment.best", "false"));
            saveSerializationType = SerializationType.valueOf(properties.getProperty("save.serialization.type", "JAVA"));
            threadPoolSize = Integer.parseInt(properties.getProperty("thread.pool.size", "10"));
            experimentsSourceType = ExperimentsSourceType.valueOf(properties.getProperty("experiments.source", "CODE"));
            experimentsSourceYamlPath = properties.getProperty("experiments.source.yaml.path", "");
            experimentsSourceYamlFilename = properties.getProperty("experiments.source.yaml.filename", "");
        } catch (IOException ex) {
            logger.severe("Ошибка при загрузке параметров: " + ex);
            throw ex;
        }
    }

    private static PrintOptions readPrintOptions(Properties properties, String name) {
        return new PrintOptions(
                Boolean.parseBoolean(properties.getProperty("print." + name, "false")),
                Boolean.parseBoolean(properties.getProperty("print." + name + ".parameters", "false")),
                Boolean.parseBoolean(properties.getProperty("print." + name + ".table", "false")),
                Boolean.parseBoolean(properties.getProperty("print." + name + ".dynamic", "false")),
                Double.parseDouble(properties.getProperty("print." + name + ".table.part", "0.1")));
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public String getDoubleFormat() {
        return doubleFormat;
    }

    public boolean isPrintRequired() {
        return printRequired;
    }

    public PrintOptions getPrintConfigurationEach() {
        return printConfigurationEach;
    }

    public PrintOptions getPrintConfigurationBest() {
        return printConfigurationBest;
    }

    public PrintOptions getPrintExperimentEach() {
        return printExperimentEach;
    }

    public PrintOptions getPrintExperimentBest() {
        return printExperimentBest;
    }

    public boolean isSaveRequired() {
        return saveRequired;
    }

    public String getSavePath() {
        return savePath;
    }

    public String getSaveFilenamePattern() {
        return saveFilenamePattern;
    }

    public boolean isSaveConfigurationEach() {
        return saveConfigurationEach;
    }

    public boolean isSaveConfigurationBest() {
        return saveConfigurationBest;
    }

    public boolean isSaveExperimentEach() {
        return saveExperimentEach;
    }

    public boolean isSaveExperimentBest() {
        return saveExperimentBest;
    }

    public SerializationType getSaveSerializationType() {
        return saveSerializationType;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public ExperimentsSourceType getExperimentsSourceType() {
        return experimentsSourceType;
    }

    public String getExperimentsSourceYamlPath() {
        return experimentsSourceYamlPath;
    }

    public String getExperimentsSourceYamlFilename() {
        return experimentsSourceYamlFilename;
    }

    @Override
    public String toString() {
        return "AppProperties{" +
                "debugMode=" + debugMode +
                ", doubleFormat='" + doubleFormat + '\'' +
                ", printRequired=" + printRequired +
                ", printConfigurationEach=" + printConfigurationEach +
                ", printConfigurationBest=" + printConfigurationBest +
                ", printExperimentEach=" + printExperimentEach +
                ", printExperimentBest=" + printExperimentBest +
                ", saveRequired=" + saveRequired +
                ", savePath='" + savePath + '\'' +
                ", saveFilenamePattern='" + saveFilenamePattern + '\'' +
                ", saveConfigurationEach=" + saveConfigurationEach +
                ", saveConfigurationBest=" + saveConfigurationBest +
                ", saveExperimentEach=" + saveExperimentEach +
                ", saveExperimentBest=" + saveExperimentBest +
                ", saveSerializationType=" + saveSerializationType +
                ", threadPoolSize=" + threadPoolSize +
                ", experimentsSourceType=" + experimentsSourceType +
                ", experimentsSourceYamlPath='" + experimentsSourceYamlPath + '\'' +
                ", experimentsSourceYamlFilename='" + experimentsSourceYamlFilename + '\'' +
                '}';
    }
}
