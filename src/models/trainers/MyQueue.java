package models.trainers;

/**
 * Структура фиксированного размера для хранения массива с перезаписью существующих элементов при добавлении новых
 * элементов, когда массив уже заполнен. Атрибуты модели:
 *  actualSize - количество вставленных элементов;
 *  size - размер структуры;
 *  values - массив элементов;
 *  lastPushed - индекс, по которому был записан последний элемент.
 */
public class MyQueue {
    private int actualSize;
    private final int size;
    private final double[] values;
    private int lastPushed;

    /**
     * Конструктор
     * @param size  размер структуры
     */
    public MyQueue(int size) {
        this.size = size;
        values = new double[size];
        lastPushed = 0;
        actualSize = 0;
    }

    public int getSize() {
        return size;
    }

    public int getActualSize() {
        return actualSize;
    }

    /**
     * Записать значение в структуру
     * @param value  значение
     */
    public void push(double value) {
        if (actualSize < size)
            actualSize++;
        lastPushed = (lastPushed + 1) % size;
        values[lastPushed] = value;
    }

    /**
     * Среднее арифметическое хранящихся значений
     * @return  среднее значение
     */
    public double mean() {
        double sum = 0.0;
        for (double value: values)
            sum += value;
        return sum / actualSize;
    }

    /**
     * Заполнить структуру максимальными значениями
     */
    public void setMax() {
        setValue(Double.MAX_VALUE);
    }

    /**
     * Заполнить структуру минимальными значениями
     */
    public void setMin() {
        setValue(Double.MIN_VALUE);
    }

    /**
     * Заполнить структуру нулями
     */
    public void setZero() {
        setValue(0.0);
    }

    /**
     * Заполнить структуру одинаковыми значениями
     * @param value  значение для заполнения
     */
    private void setValue(double value) {
        for (int i = 0; i < size; i++)
            values[i] = value;
    }
}
