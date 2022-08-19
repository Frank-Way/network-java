package options;

/**
 * Опции вывода. Параметры модели:
 *  required - требуется ли вывод;
 *  parametersRequired - требуется ли вывод параметров сети;
 *  tableRequired - требуется ли вывод результатов обучения в виде таблицы;
 *  dynamicRequired - требуется вывод динамики обучения (зависимость потери от эпохи);
 *  tablePart - какую часть таблиц печатать (1.0 - полностью, 0.5 - каждую вторую строчку и т.д.).
 */
public class PrintOptions {
    private final boolean required;
    private final boolean parametersRequired;
    private final boolean tableRequired;
    private final boolean dynamicRequired;
    private final double tablePart;

    /**
     * Конструктор
     * @param required  вывод требуется
     * @param parametersRequired  требуется вывод параметров сети
     * @param tableRequired  требуется вывод результатов обучения в виде таблицы
     * @param dynamicRequired  требуется вывод динамики обучения
     * @param tablePart  какую часть таблиц печатать
     */
    public PrintOptions(boolean required, boolean parametersRequired, boolean tableRequired,
                        boolean dynamicRequired, double tablePart) {
        this.required = required;
        this.parametersRequired = parametersRequired;
        this.tableRequired = tableRequired;
        this.dynamicRequired = dynamicRequired;
        this.tablePart = tablePart;
    }

    /**
     * Конструктор
     * @param value  значение, которое будет установлено для всех полей класса
     */
    public PrintOptions(boolean value) {
        this(value, value, value, value, 0.1);
    }

    /**
     * Конструктор
     * @param value  значение, которое будет установлено для всех полей класса
     * @param tablePart  какую часть таблиц печатать
     */
    public PrintOptions(boolean value, double tablePart) {
        this(value, value, value, value, tablePart);
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

    public double getTablePart() {
        return tablePart;
    }

    @Override
    public String toString() {
        return "PrintOptions{" +
                "required=" + required +
                ", parametersRequired=" + parametersRequired +
                ", tableRequired=" + tableRequired +
                ", dynamicRequired=" + dynamicRequired +
                ", tablePart=" + tablePart +
                '}';
    }
}
