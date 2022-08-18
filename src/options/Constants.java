package options;

import serialization.SerializationType;

import java.io.File;

/**
 * Константы для настройки того, что необходимо выводить и сохранять.
 * Параметры можно переопределить здесь или поменять конкретно в месте, где они используются.
 */
public abstract class Constants {
    public final static boolean DEBUG_MODE = false;  // режим вывода; при true выводится больше информации

    // формат вывода дробных чисел (до точки - общее количество цифр, после - количество цифр дробной части
    public final static String DOUBLE_FORMAT = "%25.20f";

    // нужно ли выводить какую-либо информацию
    // флаг объединяется с другими настройками вывода по И, так что можно в одном месте "выключить" весь вывод, не меняя
    // остальные настройки вывода
    public final static boolean PRINT_REQUIRED = true;

    /**
     * секция задания настроек, что именно выводить в разных случаях
     * для опций см. {@link PrintOptions}
     */
    // вывод по каждой попытке обучения
    public final static PrintOptions PRINT_EACH_CONFIGURATION = new PrintOptions(true);

    // вывод по наилучшей попытке обучения для каждой конфигурации
    public final static PrintOptions PRINT_EACH_CONFIGURATION_BEST = new PrintOptions(false);

    // вывод по наилучшей попытке обучения для всех конфигураций каждого эксперимента
    public final static PrintOptions PRINT_EXPERIMENT_BEST = new PrintOptions(true, 0.5);

    // вывод по наилучшей попытке обучения для всех экспериментов
    public final static PrintOptions PRINT_ALL_EXPERIMENTS_BEST = new PrintOptions(false);

    // флаг, аналогичный PRINT_REQUIRED, но для сохранения обученных сетей
    public final static boolean SAVE_REQUIRED = true;

    // папка для сохранения обученных сетей
    public final static String SAVE_FOLDER = System.getProperty("user.dir") + File.separator + "networks";
    // шаблон для имени файлов с сетями
    public final static String SAVE_NETWORK_PATTERN = "network_%s";

    // сохранение сети по попытке обучения
    public final static boolean SAVE_EACH_CONFIGURATION = false;

    // сохранение сети по наилучшей попытке обучения для каждой конфигурации
    public final static boolean SAVE_EACH_CONFIGURATION_BEST = false;

    // сохранение сети по наилучшей попытке обучения для всех конфигураций каждого эксперимента
    public final static boolean SAVE_EXPERIMENT_BEST = true;

    // сохранение сети по наилучшей попытке обучения для всех экспериментов
    public final static boolean SAVE_ALL_EXPERIMENTS_BEST = false;

    public final static SerializationType SERIALIZATION_TYPE = SerializationType.YAML;

}
