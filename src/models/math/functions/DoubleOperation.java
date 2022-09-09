package models.math.functions;

/**
 * Унарная операция с вещественным числом
 */
public interface DoubleOperation {
    /**
     * Выполнение операции
     * @param value операнд
     * @return      результат выполнения операции
     */
    double apply(double value);
}
