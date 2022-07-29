package options;

/**
 * Опции вывода. Параметры модели:
 *  required - требуется ли вывод;
 *  parametersRequired - требуется ли вывод параметров сети;
 *  tableRequired - требуется ли вывод результатов обучения в виде таблицы;
 *  dynamicRequired - требуется вывод динамики обучения (зависимость потери от эпохи).
 */
public class PrintOptions {
    private final boolean required;
    private final boolean parametersRequired;
    private final boolean tableRequired;
    private final boolean dynamicRequired;

    /**
     * Конструктор
     * @param required  вывод требуется
     * @param parametersRequired  требуется вывод параметров сети
     * @param tableRequired  требуется вывод результатов обучения в виде таблицы
     * @param dynamicRequired  требуется вывод динамики обучения (зависимость потери от эпохи)
     */
    public PrintOptions(boolean required, boolean parametersRequired, boolean tableRequired, boolean dynamicRequired) {
        this.required = required;
        this.parametersRequired = parametersRequired;
        this.tableRequired = tableRequired;
        this.dynamicRequired = dynamicRequired;
    }

    /**
     * Конструктор
     * @param value  значение, которое будет установлено для всех полей класса
     */
    public PrintOptions(boolean value) {
        this.required = value;
        this.parametersRequired = value;
        this.tableRequired = value;
        this.dynamicRequired = value;
    }

    public boolean isRequired() {
        return required;
    }

    public boolean isParametersRequired() {
        return parametersRequired;
    }

    public boolean isTableRequired() {
        return tableRequired;
    }

    public boolean isDynamicRequired() {
        return dynamicRequired;
    }
}
