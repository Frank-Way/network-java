package models.math;

import models.interfaces.Copyable;
import serialization.YamlSerializationOptions;
import serialization.YamlSerializationUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static serialization.YamlSerializationOptions.CRLF;

/**
 * Двумерная матрица вещественных чисел. Внутри используется double[][]. Реализует различные полезные методы для работы
 * с матрицами. Каждая операция создаёт новую матрицу, то есть класс не-изменяемый (immutable). Атрибуты модели
 *  values - двумерный массив, хранящий числа;
 *  rows - количество строк;
 *  cols - количество столбцов.
 */
public class Matrix implements Copyable<Matrix>, Serializable {
    private static final long serialVersionUID = -5198808201700701411L;
    private final double[][] values;
    private final int rows;
    private final int cols;

    /**
     * Конструктор
     * @param values значения
     */
    public Matrix(double[][] values) {
        this.values = values;
        this.rows = values.length;
        this.cols = values[0].length;
    }

    enum Operator {
        MUL,
        ADD,
        SUB,
        DIV
    }

    private double applyOperator(Operator operator, double value1, double value2) {
        switch (operator) {
            case ADD:
                return value1 + value2;
            case SUB:
                return value1 - value2;
            case MUL:
                return value1 * value2;
            case DIV:
                return value1 / value2;
            default:
                throw new IllegalArgumentException("Недопустимое значение operator=" + operator);
        }
    }

    private Matrix doOperation(Operator operator, Matrix matrix) {
        assertEqualRowsAndCols(matrix,
                "Матрицы размерности (%d; %d) и (%d; %d) имеют разное количество столбцов и/или строк");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = applyOperator(operator, result[row][col], matrix.values[row][col]);
        return new Matrix(result);
    }

    private Matrix doOperation(Operator operator, Number number) {
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = applyOperator(operator, result[row][col], number.doubleValue());
        return new Matrix(result);
    }

    private Matrix doColOperation(Operator operator, Matrix colMatrix) {
        assertEqualRows(colMatrix, "Матрицы размерности (%d; %d) и (%d; %d) имеют разное количество строк");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = applyOperator(operator, result[row][col], colMatrix.values[row][0]);
        return new Matrix(result);
    }

    private Matrix doRowOperation(Operator operator, Matrix rowMatrix) {
        assertEqualCols(rowMatrix, "Матрицы размерности (%d; %d) и (%d; %d) имеют разное количество столбцов");
        double[][] result = copy().values;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++)
                result[row][col] = applyOperator(operator, result[row][col], rowMatrix.values[0][col]);
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
    public Matrix copy() {
        double[][] result = new double[rows][cols];
        for (int row = 0; row < rows; row++)
            result[row] = Arrays.copyOf(values[row], cols);
        return new Matrix(result);
    }

    @Override
    public String toString() {
        return "Matrix {" +
                "rows=" + rows +
                ", cols=" + cols +
                '}';
    }

    /**
     * Формирование строки со значениями
     * @param format формат вывода вещественных чисел
     * @return строка со значениями
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
     * Формирование строки со значениями (формат вывода вещественных чисел - по умолчанию)
     * @return строка со значениями
     */
    public String valuesToString() {
        return valuesToString("%10.5f");
    }

    private double[][] getValues() {
        return this.values;
    }

    /**
     * Получение значения по строке и столбцу
     * @param row номер строки
     * @param col номер столбца
     * @return значение
     */
    public double getValue(int row, int col) {
        return values[row][col];
    }

    /**
     * Получение копии строки
     * @param row номер строки
     * @return строка
     */
    public double[] getValue(int row) {
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
     * Получение вектора-строки
     * @param row номер строки
     * @return вектор-строка
     */
    public Matrix getRow(int row) {
        double[][] result = new double[1][cols];
        for (int col = 0; col < cols; col++)
            result[0][col] = values[row][col];
        return new Matrix(result);
    }

    /**
     * Получение вектора-столбца
     * @param col номер столбца
     * @return вектор-столбец
     */
    public Matrix getCol(int col) {
        double[][] result = new double[rows][1];
        for (int row = 0; row < rows; row++)
            result[row][0] = values[row][col];
        return new Matrix(result);
    }

    /**
     * Матричное умножение
     * @param matrix вторая матрица (её количество строк должно совпадать с количеством столбцов исходной матрицы)
     * @return результат умножения (матрица размера rows1 x cols2)
     */
    public Matrix mulMatrix(Matrix matrix) {
        assertEqualColsRows(matrix,
                "Матрица размерности (%d; %d) не может быть умножена на предоставленную матрицу размера (%d; %d)");
        double[][] result = new double[rows][matrix.cols];
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < matrix.cols; col++)
                result[row][col] = MatrixOperations.mulScalar(getRow(row).transpose(), matrix.getCol(col));
        return new Matrix(result);
    }

    /**
     * Поэлементное умножение соразмерных матриц
     * @param matrix вторая матрица
     * @return результат умножения
     */
    public Matrix mul(Matrix matrix) {
        return doOperation(Operator.MUL, matrix);
    }

    /**
     * Умножение на вектор-столбец (каждый элемент строки исходной матрицы умножается на один и тот же элемент
     * вектора-столбца, соответствующий строке)
     * @param colMatrix вектор-столбец (его количество строк должно совпадать с количеством строк исходной матрицы)
     * @return результат умножения
     */
    public Matrix mulCol(Matrix colMatrix) {
        return doColOperation(Operator.MUL, colMatrix);
    }

    /**
     * Умножение на вектор-строку (каждый элемент столбца исходной матрицы умножается на один и тот же элемент
     * вектора-строки, соответствующий столбцу)
     * @param rowMatrix вектор-строка (его количество столбцов должно совпадать с количеством столбцов исходной матрицы)
     * @return результат умножения
     */
    public Matrix mulRow(Matrix rowMatrix) {
        return doRowOperation(Operator.MUL, rowMatrix);
    }

    /**
     * Умножение на скаляр
     * @param number скаляр
     * @return результат умножения
     */
    public Matrix mul(Number number) {
        return doOperation(Operator.MUL, number);
    }

    /**
     * Поэлементное сложение соразмерных матриц
     * @param matrix вторая матрица
     * @return результат сложения
     */
    public Matrix add(Matrix matrix) {
        return doOperation(Operator.ADD, matrix);
    }

    /**
     * Сложение с вектором-столбцом (см. метод mulCol)
     * @param colMatrix вектор-столбец
     * @return результат сложения
     */
    public Matrix addCol(Matrix colMatrix) {
        return doColOperation(Operator.ADD, colMatrix);
    }

    /**
     * Сложение с вектором-строкой (см. метод mulRow)
     * @param rowMatrix вектор-строка
     * @return результат сложения
     */
    public Matrix addRow(Matrix rowMatrix) {
        return doRowOperation(Operator.ADD, rowMatrix);
    }

    /**
     * Сложение со скаляром
     * @param number скаляр
     * @return результат сложения
     */
    public Matrix add(Number number) {
        return doOperation(Operator.ADD, number);
    }

    /**
     * Поэлементное вычитание соразмерных матриц
     * @param matrix вторая матрица
     * @return результат вычитания
     */
    public Matrix sub(Matrix matrix) {
        return doOperation(Operator.SUB, matrix);
    }

    /**
     * Вычитание вектора-столбца (см. метод mulCol)
     * @param colMatrix вектор-столбец
     * @return результат вычитания
     */
    public Matrix subCol(Matrix colMatrix) {
        return doColOperation(Operator.SUB, colMatrix);
    }

    /**
     * Вычитание вектора-строки (см. метод mulCol)
     * @param rowMatrix вектор-строка
     * @return результат вычитания
     */
    public Matrix subRow(Matrix rowMatrix) {
        return doRowOperation(Operator.SUB, rowMatrix);
    }

    /**
     * Вычитание скаляра
     * @param number скаляр
     * @return результат вычитания
     */
    public Matrix sub(Number number) {
        return doOperation(Operator.SUB, number);
    }

    /**
     * Поэлементное деление соразмерных матриц
     * @param matrix вторая матрица
     * @return результат деления
     */
    public Matrix div(Matrix matrix) {
        return doOperation(Operator.DIV, matrix);
    }

    /**
     * Деление на вектор-столбец (см. метод mulCol)
     * @param colMatrix вектор-столбец
     * @return результат деления
     */
    public Matrix divCol(Matrix colMatrix) {
        return doColOperation(Operator.DIV, colMatrix);
    }

    /**
     * Деление на вектор-строку (см. метод mulRow)
     * @param rowMatrix вектор-строка
     * @return результат деления
     */
    public Matrix divRow(Matrix rowMatrix) {
        return doRowOperation(Operator.DIV, rowMatrix);
    }

    /**
     * Деление на скаляр
     * @param number скаляр
     * @return результат деления
     */
    public Matrix div(Number number) {
        return doOperation(Operator.DIV, number);
    }

    /**
     * Суммирование всех элементов матрицы
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
     * @param axis ось (0 или 1)
     * @return вектор сумм
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
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    /**
     * Поиск минимального значения среди всех элементов матрицы
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
     * @param axis ось
     * @return вектор минимальных значений
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
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    /**
     * Поиск максимального значения среди всех элементов матрицы
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
     * @param axis ось
     * @return вектор максимальных значений
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
                throw new IllegalArgumentException(String.format("Недопустимое значение axis = %d", axis));
        }
    }

    /**
     * Транспонирование матрицы
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
     * @param start начало
     * @param stop конец (не включается)
     * @param step шаг
     * @return срез
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
     * @param start начало
     * @param stop конец (не включается)
     * @param step шаг
     * @return срез
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
     * @return матрица единиц
     */
    public Matrix onesLike() {
        return new Matrix(new double[rows][cols]).add(1);
    }

    /**
     * Получение матрицы такой же размерности, заполненной нулями
     * @return матрица нулей
     */
    public Matrix zerosLike() {
        return new Matrix(new double[rows][cols]);
    }

    /**
     * Получение списка с пакетами заданного размера, получаемых путем разбиения исходной матрицы
     * @param batchSize размер пакетов
     * @return список пакетов
     */
    public List<Matrix> getBatches(int batchSize) {
        List<Matrix> result = new ArrayList<>();
        for (int i = 0; i < rows; i = i + batchSize)
            result.add(getRowSlice(i, Math.min(i + batchSize, rows), 1));
        return result;
    }

    /**
     * Расширение матрицы путем заполнения пропусков одинаковыми значениями.
     * Пусть имеется матрица [[1, 2],
     *                        [3, 4]].
     * Тогда при factor = 3 и axis = 0 результат будет [[1, 2],
     *                                                  [1, 2],
     *                                                  [1, 2],
     *                                                  [3, 4],
     *                                                  [3, 4],
     *                                                  [3, 4]].
     * А при factor = 3 и axis = 1 - [[1, 1, 1, 2, 2, 2],
     *                                [3, 3, 3, 4, 4, 4]].
     * @param factor множитель
     * @param axis ось (0 или 1)
     * @return расширенная матрица
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
                throw new IllegalArgumentException(String.format(
                        "Недопустимый параметр extend axis=%d (допустимы: %d, %d)", axis, 0, 1));
        }
    }

    /**
     * Конкатенация матриц. Если ось = 0, то конкатенация горизонтальная (матрицы "складываются слева-направо").
     * Если ось = 1, то конкатенация вертикальная (матрицы "складываются сверху-вниз")
     * @param matrix вторая матрица (должны совпадать размерности - при горизонтальной столбцы, при вертикальной строки)
     * @param axis ось (0 или 1)
     * @return конкатенированная матрица
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
                throw new IllegalArgumentException(String.format(
                        "Недопустимый параметр stack axis=%d (допустимы: %d, %d)", axis, 0, 1));
        }
    }

    /**
     * Перемешивание матрицы случайным образом по строкам
     * @return перемешанная матрица
     */
    public Matrix shuffle() {
        return shuffle(0);
    }

    /**
     * Перемешивание матрицы случайным образом по заданной оси. Если ось = 0, то переставляются строки. Если ось = 1,
     * то переставляются столбцы
     * @param axis ось
     * @return перемешанная матрица
     */
    public Matrix shuffle(int axis) {
        switch (axis) {
            case 0:
                return shuffle(MatrixOperations.getRandomRangePermutation(rows), axis);
            case 1:
                return shuffle(MatrixOperations.getRandomRangePermutation(cols), axis);
            default:
                throw new IllegalArgumentException(String.format(
                        "Недопустимый параметр shuffle axis=%d (допустимы: %d, %d)", axis, 0, 1));
        }
    }

    /**
     * Перемешивание матрицы в соответствии с заданными индексами (контроль за корректностью указания индексов остаётся
     * за пользователем) по заданной оси
     * @param indices индексы
     * @param axis ось
     * @return перемешанная матрица
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
                throw new IllegalArgumentException(String.format(
                        "Недопустимый параметр shuffle axis=%d (допустимы: %d, %d)", axis, 0, 1));
        }
        return new Matrix(result);
    }

    /**
     * Получение абсолютных значений
     * @return матрица абсолютных значений
     */
    public Matrix abs() {
        return MatrixOperations.Functions.abs(this);
    }

    /**
     * Возведение в степень
     * @param scale степень
     * @return матрица, каждый элемент которой возведен в указанную степень
     */
    public Matrix pow(double scale) {
        return MatrixOperations.Functions.pow(this, scale);
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

    public String toYaml(int baseIndent, String doubleFormat) {
        StringBuilder sb = new StringBuilder();
        final String baseIndentString = YamlSerializationUtils.repeat(" ", baseIndent);
        sb.append(baseIndentString).append("class: ").append(this.getClass().getCanonicalName()).append(CRLF);
        sb.append(baseIndentString).append("rows: ").append(rows).append(CRLF);
        sb.append(baseIndentString).append("cols: ").append(cols).append(CRLF);
        sb.append(baseIndentString).append("values:").append(CRLF);
        String[] valuesSA = YamlSerializationUtils.addStringAtBegin(YamlSerializationUtils.double2DArrayToYaml(values, doubleFormat),
                baseIndentString + YamlSerializationOptions.YAML_INDENT_STRING);
        sb.append(String.join("\n", valuesSA));
        return sb.toString();
    }

    public static Matrix fromYaml(String yaml, int baseIndent) {
        final String[] patterns = {
                "class: " + Matrix.class.getCanonicalName().replace(".", "\\.") + "\\n?",
                "rows: \\d+\\n?",
                "cols: \\d+\\n?",
                "values:\\n?"};
        String[] lines = YamlSerializationUtils.removeFirstCharacters(yaml.split(CRLF), yaml.indexOf('c'));
        for (int assertNum = 0; assertNum < patterns.length; assertNum++)
            if (!lines[assertNum].matches(patterns[assertNum]))
                throw new IllegalArgumentException(String.format("Формат не верный, строка %d: %s", assertNum + 1, lines[assertNum]));
        int rows = Integer.parseInt(lines[1].split(":")[1].trim());
        int cols = Integer.parseInt(lines[2].split(":")[1].trim());

        double[][] array = YamlSerializationUtils.double2DArrayFromYaml(Arrays.copyOfRange(lines, 4, lines.length), YamlSerializationOptions.YAML_INDENT);

        int readRows = array.length;
        int readCols = array[0].length;

        if (readRows != rows || readCols != cols)
            throw new IllegalArgumentException(String.format(
                    "Количество строк/столбцов не совпадает с заявленным (указано %dx%d, считано %dx%d",
                    rows, cols, readRows, readCols));

        return new Matrix(array);
    }

    /**
     * Проверка равенства количества строк и столбцов
     * @param matrix вторая матрица
     * @return true, если матрицы соразмерны; иначе false
     */
    protected boolean isRowsAndColsEqual(Matrix matrix) {
        return isRowsEqual(matrix) && isColsEqual(matrix);
    }

    /**
     * Проверка равенства количества строк
     * @param matrix вторая матрица
     * @return true, если количество строк совпадает
     */
    protected boolean isRowsEqual(Matrix matrix) {
        return this.rows == matrix.rows;
    }

    /**
     * Проверка равенства количества столбцов
     * @param matrix вторая матрица
     * @return true, если количество столбцов совпадает
     */
    protected boolean isColsEqual(Matrix matrix) {
        return this.cols == matrix.cols;
    }

    /**
     * Проверка равенства количества строк исходной матрицы и количества столбцов второй матрицы
     * @param matrix вторая матрица
     * @return true, если количества совпадает
     */
    protected boolean isRowsColsEqual(Matrix matrix) {
        return this.rows == matrix.cols;
    }

    /**
     * Проверка равенства количества столбцов исходной матрицы и количества строк второй матрицы
     * @param matrix вторая матрица
     * @return true, если количества совпадает
     */
    protected boolean isColsRowsEqual(Matrix matrix) {
        return this.cols == matrix.rows;
    }

    /**
     * Проверка, основанная на isRowsEqual, при несовпадении выбрасывается исключение
     * @param matrix вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualRows(Matrix matrix, String message) {
        if (!isRowsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, основанная на isColsEqual, при несовпадении выбрасывается исключение
     * @param matrix вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualCols(Matrix matrix, String message) {
        if (!isColsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, основанная на isRowsColsEqual, при несовпадении выбрасывается исключение
     * @param matrix вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualRowsCols(Matrix matrix, String message) {
        if (!isRowsColsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, основанная на isColsRowsEqual, при несовпадении выбрасывается исключение
     * @param matrix вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualColsRows(Matrix matrix, String message) {
        if (!isColsRowsEqual(matrix))
            throw new IllegalArgumentException(String.format(message,
                    this.rows, this.cols, matrix.rows, matrix.cols));
    }

    /**
     * Проверка, объединяющая assertEqualRows и assertEqualCols
     * @param matrix вторая матрица
     * @param message сообщение для исключения
     */
    private void assertEqualRowsAndCols(Matrix matrix, String message) {
        assertEqualRows(matrix, message);
        assertEqualCols(matrix, message);
    }
}