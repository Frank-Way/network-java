package options;

/**
 * Класс с описанием параметров по умолчанию, использовавшиеся в основном для отладки при разработке
 */
public abstract class DefaultParameters {
    public static final int RETRIES = 1;  // количество перезапусков одной конфигурации
    public static final int SIZE = 1024;  // размер выборки
    public static final double TEST_PART = 0.5;  // часть выборки для тестов
    public static final double VALID_PART = 0.25;  // часть выборки для валидации
    public static final double EXTENDING_FACTOR = 1.15;  // во сколько раз расширяется выборка для обучения
    public static final int QUERIES = 10;  // количество опросов при обучении сети
    public static final int EPOCHS = 2000;  // количество эпох обучения
    public static final boolean PRE_TRAIN_REQUIRED = true;  // флаг необходимости предобучения
    public static final int PRE_TRAINS_COUNT = 1;  // количество попыток предобучения
    public static final double PRE_TRAIN_REDUCE_FACTOR = 10.0;  // во сколько раз снижается количество эпох при предобучении
    public static final int BATCH_SIZE = 64;  // размер пакета
    public static final boolean EARLY_STOPPING = true;  // признак ранней остановки (когда потеря увеличивается при обучении)
    public static final double START_LR = 0.1;  // начальная скорость обучения
    public static final double STOP_LR = 0.001;  // конечная скорость обучения
}
