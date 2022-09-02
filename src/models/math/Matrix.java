package models.math;

import models.math.functions.MatrixFunctions;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * Двумерная матрица вещественных чисел. Внутри используется double[][]. Реализует различные полезные методы для работы
 * с матрицами. Каждая операция создаёт новую матрицу, то есть класс не-изменяемый (immutable). Атрибуты модели:
 * <pre><ul>
 *  <li>values - двумерный массив, хранящий числа;</li>
 *  <li>rows   - количество строк;</li>
 *  <li>cols   - количество столбцов.</li>
 * </ul></pre>
 * Если матрица имеет 1 строку, то она является вектором-строкой. Если матрица имеет 1 столбец, то она является
 * вектором-строкой.
 *
 * Матрицы поддерживают базовые арифметические операции (сложение, вычитание, умножение, деление) с матрицами,
 * скалярами, векторами-столбцами, векторами-строками.
 * <pre>
 * Пример операции с матрицей:
 * |1 2 3|   |7 4 1|   | 8  6  4|
 * |4 5 6| + |8 5 2| = |12 10  8|
 * |7 8 9|   |9 6 3|   |16 14 12|
 * Пример операции со скаляром:
 * |1 2 3|       | 6  7  8|
 * |4 5 6| + 5 = | 9 10 11|
 * |7 8 9|       |12 13 14|
 * Пример операции с вектором-столбцом:
 * |1 2 3|   |4|   | 5  6  7|
 * |4 5 6| + |5| = | 9 10 11|
 * |7 8 9|   |6|   |13 14 15|
 * Пример операции с вектором-строкой:
 * |1 2 3|             | 4  7 10|
 * |4 5 6| + |3 5 7| = | 7 10 13|
 * |7 8 9|             |10 13 16|
 * </pre>
 */
@YamlSerializable
public class Matrix implements DeepCopyable, Serializable {
    @YamlField private final double[][] values;
    @YamlField private final int rows;
    @YamlField private final int cols;

    /**
     * Конструктор
     * @param values значения
     */
    public Matrix(double[][] values) {
        this.values = values;
        this.rows = values.length;
        this.cols = values[0].length;
    }

    /**
     * Конструктор для сериализации
     */
    private Matrix() {
        this(new double[1][1]);
    }

    @Override
    public Matrix deepCopy() {
        double[][] newValues = new double[rows][];
        for (int row = 0; row < rows; row++)
            newValues[row] = Arrays.copyOf(values[row], cols);
        return new Matrix(newValues);
    }

    /**
     * Перечисление операторов
     */
    private enum Operator {
        /**
         * умножение
         */
        MUL,
        /**
         * сложение
         */
        ADD,
        /**
         * вычитание
         */
        SUB,
        /**
         * деление
         */
        DIV
    }

    /**
     * Применение оператора к операндам
     * @param operator оператор
     * @param value1   операнд1
     * @param value2   операнд2
     * @return         результат применения оператора
     */
    private double applyOperator(Operator operator, double value1, double value2) {
        switch (operator) {  // перебор операторов
            case ADD:
                return value1 + value2;
            case SUB:
                return value1 - value2;
            case MUL:
                return value1 * value2;
            case DIV:
                return value1 / value2;
            default:  // не известный оператор
                throw ExceptionUtils.newUnknownEnumItemException(Operator.class, operator);
        }
    }

    /**
     * Выполнение операции с матрицей
     * @param operator оператор
     * @param matrix   вторая матрица
     * @return         результат выполнения операции
     */
    private Matrix doOperation(Operator operator, Matrix matrix) {
        assertEqualRowsAndCols(matrix,
                "Матрицы размерности (%d; %d) и (%d; %d) имеют разное количество столбцов и/или строк");
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = applyOperator(operator, values[row][col], matrix.values[row][col]);
        return new Matrix(result);
    }

    /**
     * Выполнение операции с числом
     * @param operator оператор
     * @param number   число
     * @return         результат выполнения операции
     */
    private Matrix doOperation(Operator operator, Number number) {
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = applyOperator(operator, values[row][col], number.doubleValue());
        return new Matrix(result);
    }

    /**
     * Выполнение операции с вектором-столбцом
     * @param operator  оператор
     * @param colMatrix вектор-столбец
     * @return          результат выполнения операции
     */
    private Matrix doColOperation(Operator operator, Matrix colMatrix) {
        assertEqualRows(colMatrix, "Матрицы размерности (%d; %d) и (%d; %d) имеют разное количество строк");
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = applyOperator(operator, values[row][col], colMatrix.values[row][0]);
        return new Matrix(result);
    }

    /**
     * Выполнение операции с вектором-строкой
     * @param operator  оператор
     * @param rowMatrix вектор-строка
     * @return          результат выполнения операции
     */
    private Matrix doRowOperation(Operator operator, Matrix rowMatrix) {
        assertEqualCols(rowMatrix, "Матрицы размерности (%d; %d) и (%d; %d) имеют разное количество столбцов");
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = applyOperator(operator, values[row][col], rowMatrix.values[0][col]);
        return new Matrix(result);
    }

    /**
     * Является ли матрица вектором-строкой
     * @return результат проверки (количество строк = 1)
     */
    public boolean isRow() {
        return rows == 1;
    }

    /**
     * Является ли матрица вектором-столбцом
     * @return результат проверки (количество столбцов = 1)
     */
    public boolean isCol() {
        return cols == 1;
    }

    @Override
    public String toString() {
        return "Matrix {" +
                "rows=" + rows +
                ", cols=" + cols +
//                ", values=" + valuesToStringOneLine() +
                '}';
    }

    /**
     * Формирование строки со значениями
     * @param format формат вывода вещественных чисел
     * @return       строка со значениями
     */
    public String valuesToString(String format) {
        StringBuilder result = new StringBuilder();
        result.append('[');
        for (int row = 0; row < rows; row++) {
            if (row > 0)
                result.append(' ');
            result.append('[');
            for (int col = 0; col < cols; col++)
                result.append(String.format(format, values[row][col]));
            result.append(']');
            if (row < rows - 1)
                result.append('\n');
        }
        result.append(']');
        return result.toString();
    }

    /**
     * Формирование строки со значениями в одну линию
     * @param format формат вывода вещественных чисел
     * @return       строка со значениями
     */
    public String valuesToStringOneLine(String format) {
        StringBuilder result = new StringBuilder();
        result.append('[');
        for (int row = 0; row < rows; row++) {
            result.append('[');
            for (int col = 0; col < cols; col++)
                result.append(String.format(format, values[row][col])).append(", ");
            result.delete(result.length() - 2, result.length());
            result.append("], ");
        }
        result.delete(result.length() - 2, result.length());
        result.append(']');
        return result.toString();
    }

    /**
     * Формирование строки со значениями (формат вывода вещественных чисел - по умолчанию)
     * @return строка со значениями
     */
    public String valuesToString() {
        return valuesToString("%10.5f");
    }

    public String valuesToStringOneLine() {
        return valuesToStringOneLine("%10.5f");
    }

    private double[][] getValues() {
        return this.values;
    }

    /**
     * Получение значения по строке и столбцу
     * @param row номер строки
     * @param col номер столбца
     * @return    значение
     */
    public double getValue(int row, int col) {
        return values[row][col];
    }

    /**
     * Получение строки
     * @param row номер строки
     * @return    строка
     */
    public double[] getValue(int row) {
        // возвращается копия
        return Arrays.copyOf(values[row], cols);
    }

    /**
     * Количество элементов матрицы
     * @return размер
     */
    public int size() {
        return rows * cols;
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    /**
     * Получение вектора-строки из матрицы
     * <pre>
     * Пример для row=1:
     * |1 2 3|
     * |4 5 6| --> |4 5 6|
     * |7 8 9|
     * </pre>
     * @param row номер строки
     * @return    вектор-строка
     */
    public Matrix getRow(int row) {
        double[][] result = new double[1][cols];
        for (int col = 0; col < cols; col++)
            result[0][col] = values[row][col];
        return new Matrix(result);
    }

    /**
     * Получение вектора-столбца из матрицы
     * <pre>
     * Пример для col=1:
     * |1 2 3|     |2|
     * |4 5 6| --> |5|
     * |7 8 9|     |8|
     * </pre>
     * @param col номер столбца
     * @return    вектор-столбец
     */
    public Matrix getCol(int col) {
        double[][] result = new double[rows][1];
        for (int row = 0; row < rows; row++)
            result[row][0] = values[row][col];
        return new Matrix(result);
    }

    /**
     * Матричное умножение
     * <pre>
     * Пример:
     * | 1  2  3|           | 22  28|
     * | 4  5  6|   |1 2|   | 49  64|
     * | 7  8  9| x |3 4| = | 76 100|
     * |10 11 12|   |5 6|   |103 136|
     * |13 14 15|           |130 172|
     * </pre>
     * @param matrix вторая матрица (её количество строк должно совпадать с количеством столбцов исходной матрицы)
     * @return       результат умножения (матрица размера rows1 x cols2)
     */
    public Matrix mulMatrix(Matrix matrix) {
        assertEqualColsRows(matrix,
                "Матрица размерности (%d; %d) не может быть умножена на предоставленную матрицу размера (%d; %d)");
        double[][] result = new double[rows][matrix.cols];
        Matrix[] rows1 = IntStream.range(0, this.rows).mapToObj(row -> this.getRow(row).transpose()).toArray(Matrix[]::new);
        Matrix[] cols2 = IntStream.range(0, matrix.cols).mapToObj(col -> matrix.getCol(col)).toArray(Matrix[]::new);
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < matrix.cols; col++)
                result[row][col] = MatrixUtils.mulScalar(rows1[row], cols2[col]);
        return new Matrix(result);
    }

    /**
     * Поэлементное умножение соразмерных матриц
     * @param matrix вторая матрица
     * @return       результат умножения
     */
    public Matrix mul(Matrix matrix) {
        return doOperation(Operator.MUL, matrix);
    }

    /**
     * Умножение на вектор-столбец (каждый элемент строки исходной матрицы умножается на один и тот же элемент
     * вектора-столбца, соответствующий строке)
     * @param colMatrix вектор-столбец (его количество строк должно совпадать с количеством строк исходной матрицы)
     * @return          результат умножения
     */
    public Matrix mulCol(Matrix colMatrix) {
        return doColOperation(Operator.MUL, colMatrix);
    }

    /**
     * Умножение на вектор-строку (каждый элемент столбца исходной матрицы умножается на один и тот же элемент
     * вектора-строки, соответствующий столбцу)
     * @param rowMatrix вектор-строка (его количество столбцов должно совпадать с количеством столбцов исходной матрицы)
     * @return          результат умножения
     */
    public Matrix mulRow(Matrix rowMatrix) {
        return doRowOperation(Operator.MUL, rowMatrix);
    }

    /**
     * Умножение на скаляр
     * @param number скаляр
     * @return       результат умножения
     */
    public Matrix mul(Number number) {
        return doOperation(Operator.MUL, number);
    }

    /**
     * Поэлементное сложение соразмерных матриц
     * @param matrix вторая матрица
     * @return       результат сложения
     */
    public Matrix add(Matrix matrix) {
        return doOperation(Operator.ADD, matrix);
    }

    /**
     * Сложение с вектором-столбцом (см. метод mulCol)
     * @param colMatrix вектор-столбец
     * @return          результат сложения
     */
    public Matrix addCol(Matrix colMatrix) {
        return doColOperation(Operator.ADD, colMatrix);
    }

    /**
     * Сложение с вектором-строкой (см. метод mulRow)
     * @param rowMatrix вектор-строка
     * @return          результат сложения
     */
    public Matrix addRow(Matrix rowMatrix) {
        return doRowOperation(Operator.ADD, rowMatrix);
    }

    /**
     * Сложение со скаляром
     * @param number скаляр
     * @return       результат сложения
     */
    public Matrix add(Number number) {
        return doOperation(Operator.ADD, number);
    }

    /**
     * Поэлементное вычитание соразмерных матриц
     * @param matrix вторая матрица
     * @return       результат вычитания
     */
    public Matrix sub(Matrix matrix) {
        return doOperation(Operator.SUB, matrix);
    }

    /**
     * Вычитание вектора-столбца (см. метод mulCol)
     * @param colMatrix вектор-столбец
     * @return          результат вычитания
     */
    public Matrix subCol(Matrix colMatrix) {
        return doColOperation(Operator.SUB, colMatrix);
    }

    /**
     * Вычитание вектора-строки (см. метод mulCol)
     * @param rowMatrix вектор-строка
     * @return          результат вычитания
     */
    public Matrix subRow(Matrix rowMatrix) {
        return doRowOperation(Operator.SUB, rowMatrix);
    }

    /**
     * Вычитание скаляра
     * @param number скаляр
     * @return       результат вычитания
     */
    public Matrix sub(Number number) {
        return doOperation(Operator.SUB, number);
    }

    /**
     * Поэлементное деление соразмерных матриц
     * @param matrix вторая матрица
     * @return       результат деления
     */
    public Matrix div(Matrix matrix) {
        return doOperation(Operator.DIV, matrix);
    }

    /**
     * Деление на вектор-столбец (см. метод mulCol)
     * @param colMatrix вектор-столбец
     * @return          результат деления
     */
    public Matrix divCol(Matrix colMatrix) {
        return doColOperation(Operator.DIV, colMatrix);
    }

    /**
     * Деление на вектор-строку (см. метод mulRow)
     * @param rowMatrix вектор-строка
     * @return          результат деления
     */
    public Matrix divRow(Matrix rowMatrix) {
        return doRowOperation(Operator.DIV, rowMatrix);
    }

    /**
     * Деление на скаляр
     * @param number скаляр
     * @return       результат деления
     */
    public Matrix div(Number number) {
        return doOperation(Operator.DIV, number);
    }

    /**
     * Суммирование всех элементов матрицы
     * <pre>
     * Пример:
     * |1 2 3|
     * |4 5 6| --> 45
     * |7 8 9|
     * </pre>
     * @return сумма
     */
    public double sum() {
        double result = 0.0;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result += values[row][col];
        return result;
    }

    /**
     * Суммирование матрицы по осям. Если ось = 0, то в результате будет получен вектор-столбец, каждый элемент которого
     * является суммой соответствующей строки исходной матрицы. Если ось = 1, то в результате будет получен-вектор
     * строка, каждый элемент которого является суммой соответствующего столбца исходной матрицы.
     * <pre>
     * Пример для axis=0:
     * |1 2 3|     | 6|
     * |4 5 6| --> |15|
     * |7 8 9|     |24|
     *
     * Пример для axis=1:
     * |1 2 3|
     * |4 5 6| --> |12 15 18|
     * |7 8 9|
     * </pre>
     * @param axis ось (0 или 1)
     * @return     вектор сумм
     */
    public Matrix sum(int axis) {
        double[][] result;
        double s;
        switch (axis) {
            case 0:
                result = new double[rows][1];
                for (int row = 0; row < rows; row++) {
                    s = 0;
                    for (int col = 0; col < cols; col++)
                        s += values[row][col];
                    result[row][0] = s;
                }
                return new Matrix(result);
            case 1:
                result = new double[1][cols];
                for (int col = 0; col < cols; col++) {
                    s = 0;
                    for (int row = 0; row < rows; row++)
                        s += values[row][col];
                    result[0][col] = s;
                }
                return new Matrix(result);
            default:
                throw ExceptionUtils.newUnknownAxisException(axis, 2);
        }
    }

    /**
     * Поиск минимального значения среди всех элементов матрицы
     * <pre>
     * Пример:
     * |1 2 3|
     * |4 5 6| --> 1
     * |7 8 9|
     * </pre>
     * @return минимальное значение
     */
    public double min() {
        double result = Double.MAX_VALUE;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result = Math.min(result, values[row][col]);
        return result;
    }

    /**
     * Поиск минимального значения по осям (см. метод sum(int axis))
     * <pre>
     * Пример для axis=0:
     * |1 2 3|     |1|
     * |4 5 6| --> |4|
     * |7 8 9|     |7|
     *
     * Пример для axis=1:
     * |1 2 3|
     * |4 5 6| --> |1 2 3|
     * |7 8 9|
     * </pre>
     * @param axis ось
     * @return     вектор минимальных значений
     */
    public Matrix min(int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[rows][1];
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++)
                        result[row][0] = Math.min(result[row][0], values[row][col]);
                }
                return new Matrix(result);
            case 1:
                result = new double[1][cols];
                for (int col = 0; col < cols; col++) {
                    for (int row = 0; row < rows; row++)
                        result[0][col] = Math.min(result[0][col], values[row][col]);
                }
                return new Matrix(result);
            default:
                throw ExceptionUtils.newUnknownAxisException(axis, 2);
        }
    }

    /**
     * Поиск максимального значения среди всех элементов матрицы
     * <pre>
     * Пример:
     * |1 2 3|
     * |4 5 6| --> 9
     * |7 8 9|
     * </pre>
     * @return максимальное значение
     */
    public double max() {
        double result = Double.MIN_VALUE;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result = Math.max(result, values[row][col]);
        return result;
    }

    /**
     * Поиск максимального значения по осям (см. метод sum(int axis))
     * <pre>
     * Пример для axis=0:
     * |1 2 3|     |3|
     * |4 5 6| --> |6|
     * |7 8 9|     |9|
     *
     * Пример для axis=1:
     * |1 2 3|
     * |4 5 6| --> |7 8 9|
     * |7 8 9|
     * </pre>
     * @param axis ось
     * @return     вектор максимальных значений
     */
    public Matrix max(int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[rows][1];
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < cols; col++)
                        result[row][0] = Math.max(result[row][0], values[row][col]);
                }
                return new Matrix(result);
            case 1:
                result = new double[1][cols];
                for (int col = 0; col < cols; col++) {
                    for (int row = 0; row < rows; row++)
                        result[0][col] = Math.max(result[0][col], values[row][col]);
                }
                return new Matrix(result);
            default:
                throw ExceptionUtils.newUnknownAxisException(axis, 2);
        }
    }

    /**
     * Транспонирование матрицы
     * <pre>
     * Пример:
     * |1 2|     |1 3 5|
     * |3 4| --> |2 4 6|
     * |5 6|
     * </pre>
     * @return транспонированная матрица
     */
    public Matrix transpose() {
        double[][] result = new double[cols][rows];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[col][row] = values[row][col];
        return new Matrix(result);
    }

    /**
     * Получение среза по строкам
     * <pre>
     * Пример для start=1, stop=4, step=1:
     * | 1  2  3|
     * | 4  5  6|     | 4  5  6|
     * | 7  8  9| --> | 7  8  9|
     * |10 11 12|     |10 11 12|
     * |13 14 15|
     * </pre>
     * @param start начало
     * @param stop  конец (не включается)
     * @param step  шаг
     * @return      срез
     */
    public Matrix getRowSlice(int start, int stop, int step) {
        if (start > stop || start > rows || stop > rows || step < 1)
            throw new IllegalArgumentException(String.format(
                    "Недопустимые аргументы для среза (start=%d, stop=%d, step=%d)", start, stop, step));
        int rows = (int)Math.ceil((stop - start) * 1.0 / step);
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = values[start + row * step][col];
        return new Matrix(result);
    }

    /**
     * Получение среза по столбцам
     * <pre>
     * Пример для start=1, stop=3, step=1:
     * | 1  2  3|     | 2  3|
     * | 4  5  6|     | 5  6|
     * | 7  8  9| --> | 8  9|
     * |10 11 12|     |11 12|
     * |13 14 15|     |14 15|
     * </pre>
     * @param start начало
     * @param stop  конец (не включается)
     * @param step  шаг
     * @return      срез
     */
    public Matrix getColSlice(int start, int stop, int step) {
        if (start > stop || start > cols || stop > cols || step < 1)
            throw new IllegalArgumentException(String.format(
                    "Недопустимые аргументы для среза (start=%d, stop=%d, step=%d)", start, stop, step));
        int cols = (int)Math.ceil((stop - start) * 1.0 / step);
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = values[row][start + col * step];
        return new Matrix(result);
    }

    /**
     * Получение матрицы такой же размерности, заполненной единицами
     * <pre>
     * Пример:
     * |1 2 3|     |1 1 1|
     * |4 5 6| --> |1 1 1|
     * |7 8 9|     |1 1 1|
     * </pre>
     * @return матрица единиц
     */
    public Matrix onesLike() {
        return zerosLike().add(1);
    }

    /**
     * Получение матрицы такой же размерности, заполненной нулями
     * <pre>
     * Пример:
     * |1 2 3|     |0 0 0|
     * |4 5 6| --> |0 0 0|
     * |7 8 9|     |0 0 0|
     * </pre>
     * @return матрица нулей
     */
    public Matrix zerosLike() {
        return new Matrix(new double[rows][cols]);
    }

    /**
     * Расширение матрицы путем заполнения пропусков одинаковыми значениями
     * <pre>
     * Пример для factor=2, axis=0:
     * |1 2|     |1 2|
     * |3 4| --> |1 2|
     *           |3 4|
     *           |3 4|
     *
     * Пример для factor=2, axis=1:
     * |1 2|     |1 1 2 2|
     * |3 4| --> |3 3 4 4|
     * </pre>
     * @param factor множитель
     * @param axis   ось (0 или 1)
     * @return       расширенная матрица
     */
    public Matrix extend(int factor, int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                result = new double[rows * factor][cols];
                for (int row = 0; row < rows; row++)
                    for (int col = 0; col < cols; col++)
                        for (int i = 0; i < factor; i++)
                            result[row * factor + i][col] = values[row][col];
                return new Matrix(result);
            case 1:
                result = new double[rows][cols * factor];
                for (int row = 0; row < rows; row++)
                    for (int col = 0; col < cols; col++)
                        for (int i = 0; i < factor; i++)
                            result[row][col * factor + i] = values[row][col];
                return new Matrix(result);
            default:
                throw ExceptionUtils.newUnknownAxisException(axis, 2);
        }
    }

    /**
     * Конкатенация матриц. Если ось = 0, то конкатенация горизонтальная (матрицы "складываются слева-направо").
     * Если ось = 1, то конкатенация вертикальная (матрицы "складываются сверху-вниз")
     * <pre>
     * Пример axis=0:
     * |1 2|       |5 6 7|   |1 2 5 6 7|
     * |3 4| stack |8 9 0| = |3 4 8 9 0|
     *
     * Пример для axis=1:
     * |1 2|       |5 6|   |1 2|
     * |3 4| stack |7 8| = |3 4|
     *             |9 0|   |5 6|
     *                     |7 8|
     *                     |9 0|
     * </pre>
     * @param matrix вторая матрица (должны совпадать размерности - при горизонтальной столбцы, при вертикальной строки)
     * @param axis   ось (0 или 1)
     * @return       конкатенированная матрица
     */
    public Matrix stack(Matrix matrix, int axis) {
        double[][] result;
        switch (axis) {
            case 0:
                assertEqualRows(matrix,
                        "Матрица размерностью (%d; %d) не может быть горизонтально конкатенирована" +
                                " с матрицей размерности (%d; %d)");
                result = new double[rows][cols + matrix.cols];
                for (int row = 0; row < rows; row++) {
                    for (int col1 = 0; col1 < cols; col1++)
                        result[row][col1] = values[row][col1];

                    for (int col2 = 0; col2 < matrix.cols; col2++)
                        result[row][cols + col2] = matrix.values[row][col2];
                }
                return new Matrix(result);
            case 1:
                assertEqualCols(matrix,
                        "Матрица размерностью (%d; %d) не может быть вертикально конкатенирована" +
                                " с матрицей размерности (%d; %d)");
                result = new double[rows + matrix.rows][cols];
                for (int col = 0; col < cols; col++) {
                    for (int row1 = 0; row1 < rows; row1++)
                        result[row1][col] = values[row1][col];
                    for (int row2 = 0; row2 < matrix.rows; row2++)
                        result[rows + row2][col] = matrix.values[row2][col];
                }
                return new Matrix(result);
            default:
                throw ExceptionUtils.newUnknownAxisException(axis, 2);
        }
    }

    /**
     * Перемешивание матрицы случайным образом
     * <pre>
     * Пример:
     * |1 2 3|     |8 3 4|
     * |4 5 6| --> |5 7 1|
     * |7 8 9|     |9 2 6|
     * </pre>
     * @return перемешанная матрица
     */
    public Matrix shuffle() {
        return flatten().   // матрица "выпрямляется", становится вектором-столбцом
                shuffle(0)  // строки вектора-столбца перемешиваются
                .reshape(rows, cols);  // из вектора-столбца собирается матрица такой же формы, как и исходная
    }

    /**
     * Получение вектора-столбца из матрицы
     * <pre>
     * Пример:
     * |1 2| --> |1|
     * |3 4|     |2|
     *           |3|
     *           |4|
     * </pre>
     * @return вектор-столбец со всеми элементами матрицы
     */
    public Matrix flatten() {
        double[][] result = new double[size()][1];
        int cnt = 0;  // количество скопированных элементов
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[cnt++][0] = values[row][col];
        return new Matrix(result);
    }

    /**
     * Изменение формы матрицы. Контроль за указанием правильных размеров остается за пользователем
     * <pre>
     * Пример для newRows=3, newCols=4:
     * |1 2 3  4  5  6|     |1  2  3  4|
     * |7 8 9 10 11 12| --> |5  6  7  8|
     *                      |9 10 11 12|
     * </pre>
     * @param newRows требуемое количество строк
     * @param newCols требуемое количество столбцов
     * @return        матрица размерности (newRows; newCols)
     */
    public Matrix reshape(int newRows, int newCols) {
        int newSize = newRows * newCols;
        if (newSize != size() || newRows < 1 || newCols < 1)
            throw new IllegalArgumentException(String.format(
                    "Матрица формы (%d; %d) не может быть преобразована к (%d; %d)",
                    rows, cols, newRows, newCols));
        double[][] result = new double[newRows][newCols];
        Matrix flat = flatten();
        int cnt = 0;  // количество скопированных элементов
        for (int row = 0; row < newRows; row++)
            for (int col = 0; col < newCols; col++)
                result[row][col] = flat.values[cnt++][0];
        return new Matrix(result);
    }

    /**
     * Перемешивание матрицы случайным образом по заданной оси.
     * Если ось = 0, то переставляются строки. Если ось = 1, то переставляются столбцы
     * <pre>
     * Пример для axis=0:
     * |1 2 3|     |4 5 6|
     * |4 5 6| --> |1 2 3|
     * |7 8 9|     |7 8 9|
     *
     * Пример для axis=1:
     * |1 2 3|     |2 1 6|
     * |4 5 6| --> |5 4 3|
     * |7 8 9|     |8 7 9|
     * </pre>
     * @param axis ось
     * @return     перемешанная матрица
     */
    public Matrix shuffle(int axis) {
        switch (axis) {
            case 0:
                return shuffle(MatrixUtils.getRandomRangePermutation(rows), axis);
            case 1:
                return shuffle(MatrixUtils.getRandomRangePermutation(cols), axis);
            default:
                throw ExceptionUtils.newUnknownAxisException(axis, 2);
        }
    }

    /**
     * Перемешивание матрицы в соответствии с заданными индексами (контроль за корректностью указания индексов остаётся
     * за пользователем) по заданной оси
     * <pre>
     * Пример для indices=[3,2,1], axis=0:
     * |1 2 3|     |7 8 9|
     * |4 5 6| --> |4 5 6|
     * |7 8 9|     |1 2 3|
     *
     * Пример для indices=[3,2,1], axis=1:
     * |1 2 3|     |3 2 1|
     * |4 5 6| --> |6 5 4|
     * |7 8 9|     |9 8 7|
     * </pre>
     * @param indices индексы
     * @param axis    ось
     * @return        перемешанная матрица
     */
    public Matrix shuffle(int[] indices, int axis) {
        double[][] result = new double[rows][cols];
        switch (axis) {
            case 0:
                if (indices.length != rows)
                    throw new IllegalArgumentException(String.format(
                            "Количество индексов должно совпадать с количеством строк (получено: %d, %d)",
                            indices.length, rows));
                for (int row = 0; row < rows; row++)
                    result[row] = getValue(indices[row]);
                break;
            case 1:
                if (indices.length != cols)
                    throw new IllegalArgumentException(String.format(
                            "Количество индексов должно совпадать с количеством столбцов (получено: %d, %d)",
                            indices.length, cols));
                for (int row = 0; row < rows; row++)
                    for (int col = 0; col < cols; col++)
                        result[row][col] = getValue(row, indices[col]);
                break;
            default:
                throw ExceptionUtils.newUnknownAxisException(axis, 2);
        }
        return new Matrix(result);
    }

    /**
     * Получение абсолютных значений
     * <pre>
     * Пример:
     * |-1  2| --> |1 2|
     * | 3 -4|     |3 4|
     * </pre>
     * @return матрица абсолютных значений
     */
    public Matrix abs() {
        return MatrixFunctions.abs(this);
    }

    /**
     * Возведение в степень
     * <pre>
     * Пример для scale=2:
     * |1 2| --> |1  4|
     * |3 4|     |9 16|
     * </pre>
     * @param scale степень
     * @return      матрица, каждый элемент которой возведен в указанную степень
     */
    public Matrix pow(double scale) {
        return MatrixFunctions.pow(this, scale);
    }

    /**
     * Проверка совпадения размерностей, при несовпадении выбрасывается исключение
     * @param matrix вторая матрица
     */
    public void assertSameShape(Matrix matrix) {
        if (!isRowsAndColsEqual(matrix))
            throw new IllegalArgumentException(String.format("Формы матриц (%d; %d) и (%d; %d) не совпадает",
                    rows, cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, является ли матрица вектором-столбцом, при несовпадении выбрасывается исключение
     */
    public void assertColMatrix() {
        if (!isCol())
            throw new IllegalArgumentException(String.format("Матрица размерности (%d; %d) не является столбцом",
                    rows, cols));
    }

    /**
     * Проверка, является ли матрица вектором-строкой, при несовпадении выбрасывается исключение
     */
    public void assertRowMatrix() {
        if (!isRow())
            throw new IllegalArgumentException(String.format("Матрица размерности (%d; %d) не является строкой",
                    rows, cols));
    }

    /**
     * Проверка равенства количества строк и столбцов
     * @param matrix вторая матрица
     * @return       true, если матрицы соразмерны; иначе false
     */
    protected boolean isRowsAndColsEqual(Matrix matrix) {
        return isRowsEqual(matrix) && isColsEqual(matrix);
    }

    /**
     * Проверка равенства количества строк
     * @param matrix вторая матрица
     * @return       true, если количество строк совпадает
     */
    protected boolean isRowsEqual(Matrix matrix) {
        return this.rows == matrix.rows;
    }

    /**
     * Проверка равенства количества столбцов
     * @param matrix вторая матрица
     * @return       true, если количество столбцов совпадает
     */
    protected boolean isColsEqual(Matrix matrix) {
        return this.cols == matrix.cols;
    }

    /**
     * Проверка равенства количества строк исходной матрицы и количества столбцов второй матрицы
     * @param matrix вторая матрица
     * @return       true, если количества совпадает
     */
    protected boolean isRowsColsEqual(Matrix matrix) {
        return this.rows == matrix.cols;
    }

    /**
     * Проверка равенства количества столбцов исходной матрицы и количества строк второй матрицы
     * @param matrix вторая матрица
     * @return       true, если количества совпадает
     */
    protected boolean isColsRowsEqual(Matrix matrix) {
        return this.cols == matrix.rows;
    }

    /**
     * Проверка, основанная на isRowsEqual, при несовпадении выбрасывается исключение
     * @param matrix  вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualRows(Matrix matrix, String message) {
        if (!isRowsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, основанная на isColsEqual, при несовпадении выбрасывается исключение
     * @param matrix  вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualCols(Matrix matrix, String message) {
        if (!isColsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, основанная на isRowsColsEqual, при несовпадении выбрасывается исключение
     * @param matrix  вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualRowsCols(Matrix matrix, String message) {
        if (!isRowsColsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, основанная на isColsRowsEqual, при несовпадении выбрасывается исключение
     * @param matrix  вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualColsRows(Matrix matrix, String message) {
        if (!isColsRowsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, объединяющая assertEqualRows и assertEqualCols
     * @param matrix  вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualRowsAndCols(Matrix matrix, String message) {
        assertEqualRows(matrix, message);
        assertEqualCols(matrix, message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Matrix matrix = (Matrix) o;
        return rows == matrix.rows && cols == matrix.cols && equalValues(matrix);
    }

    /**
     * Сравнение значений матрицы с погрешностью по умолчанию 1e-6
     * @param matrix матрица для сравнения
     * @return       true, если максимальная разница соответствующих значений меньше 1e-6
     */
    public boolean equalValues(Matrix matrix) {
        return equalValues(matrix, 1e-6);
    }

    /**
     * Сравнение значений матрицы с заданной погрешностью
     * @param matrix  матрица для сравнения
     * @param epsilon погрешность
     * @return        true, если максимальная разница соответствующих значений меньше epsilon
     */
    public boolean equalValues(Matrix matrix, double epsilon) {
        assertEqualRowsAndCols(matrix, "Матрицы не совпадают размерностями: %dx%d и %dx%d");
        return this.sub(matrix).abs().max() < epsilon;
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(rows, cols);
        result = 31 * result + Arrays.deepHashCode(values);
        return result;
    }
}