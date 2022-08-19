package options;

import serialization.SerializationType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class AppProperties {
    private final static Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private final static String userDir = System.getProperty("user.dir");
    private final static String propertiesFileName = userDir + File.separator + "src" +
            File.separator + "app.properties";

    private final boolean debugMode;  // режим вывода; при true выводится больше информации

    // формат вывода дробных чисел (до точки - общее количество цифр, после - количество цифр дробной части
    private final String doubleFormat;

    // нужно ли выводить какую-либо информацию
    // флаг объединяется с другими настройками вывода по И, так что можно в одном месте "выключить" весь вывод, не меняя
    // остальные настройки вывода
    private final boolean printRequired;

    /**
     * секция задания настроек, что именно выводить в разных случаях
     * для опций см. {@link PrintOptions}
     */
    // вывод по каждой попытке обучения
    private final PrintOptions printConfigurationEach;

    // вывод по наилучшей попытке обучения для каждой конфигурации
    private final PrintOptions printConfigurationBest;

    // вывод по наилучшей попытке обучения для всех конфигураций каждого эксперимента
    private final PrintOptions printExperimentEach;

    // вывод по наилучшей попытке обучения для всех экспериментов
    private final PrintOptions printExperimentBest;

    // флаг, аналогичный PRINT_REQUIRED, но для сохранения обученных сетей
    private final boolean saveRequired;

    // папка для сохранения обученных сетей
    private final String saveFolder;
    // шаблон для имени файлов с сетями
    private final String saveFilenamePattern;

    // сохранение сети по попытке обучения
    private final boolean saveConfigurationEach;

    // сохранение сети по наилучшей попытке обучения для каждой конфигурации
    private final boolean saveConfigurationBest;

    // сохранение сети по наилучшей попытке обучения для всех конфигураций каждого эксперимента
    private final boolean saveExperimentEach;

    // сохранение сети по наилучшей попытке обучения для всех экспериментов
    private final boolean saveExperimentBest;

    // тип сериализации
    private final SerializationType saveSerializationType;

    // максимальный размер пула тредов
    private final int threadPoolSize;

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
            saveFolder = userDir + File.separator + properties.getProperty("save.folder", "networks");
            saveFilenamePattern = properties.getProperty("save.filename.pattern", "network_%s");
            saveConfigurationEach = Boolean.parseBoolean(properties.getProperty("save.configuration.each", "false"));
            saveConfigurationBest = Boolean.parseBoolean(properties.getProperty("save.configuration.best", "false"));
            saveExperimentEach = Boolean.parseBoolean(properties.getProperty("save.experiment.each", "false"));
            saveExperimentBest = Boolean.parseBoolean(properties.getProperty("save.experiment.best", "false"));
            saveSerializationType = SerializationType.valueOf(properties.getProperty("save.serialization.type", "YAML"));
//            saveSerializationType = SerializationType.YAML;
            threadPoolSize = Integer.parseInt(properties.getProperty("thread.pool.size", "10"));

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

    public String getSaveFolder() {
        return saveFolder;
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
                ", saveFolder='" + saveFolder + '\'' +
                ", saveFilenamePattern='" + saveFilenamePattern + '\'' +
                ", saveConfigurationEach=" + saveConfigurationEach +
                ", saveConfigurationBest=" + saveConfigurationBest +
                ", saveExperimentEach=" + saveExperimentEach +
                ", saveExperimentBest=" + saveExperimentBest +
                ", saveSerializationType=" + saveSerializationType +
                ", threadPoolSize=" + threadPoolSize +
                '}';
    }
}
