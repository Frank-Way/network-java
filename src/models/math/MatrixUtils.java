package models.math;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

/**
 * Набор операций с матрицами
 */
public abstract class MatrixUtils {

    /**
     * Получение случайной матрицы с равномерным распределением с заданными параметрами
     * @param rows количество строк
     * @param cols количество столбцов
     * @param location математическое ожидание
     * @param scale множитель (по умолчанию значения генерируются из диапазона [0; 1], множитель позволяет его расширить)
     * @return случайная матрица
     */
    public static Matrix getRandomMatrixUniform(int rows, int cols, double location, double scale) {
        return getRandomMatrix(RandomType.UNIFORM, rows, cols, location, scale);
    }

    /**
     * Получение случайной матрицы с равномерным распределением с параметрами по умолчанию
     * @param rows количество строк
     * @param cols количество столбцов
     * @return случайная матрица
     */
    public static Matrix getRandomMatrixUniform(int rows, int cols) {
        return getRandomMatrixUniform(rows, cols, 0.0, 1.0);
    }

    /**
     * Получение случайной матрицы с нормальным распределением с заданными параметрами
     * @param rows количество строк
     * @param cols количество столбцов
     * @param location математическое ожидание
     * @param scale среднеквадратическое отклонение
     * @return случайная матрица
     */
    public static Matrix getRandomMatrixNormal(int rows, int cols, double location, double scale) {
        return getRandomMatrix(RandomType.NORMAL, rows, cols, location, scale);
    }

    /**
     * Получение случайной матрицы с нормальным распределением с параметрами по умолчанию
     * @param rows количество строк
     * @param cols количество столбцов
     * @return случайная матрица
     */
    public static Matrix getRandomMatrixNormal(int rows, int cols) {
        return getRandomMatrixNormal(rows, cols, 0.0, 1.0);
    }

    /**
     * Получение диапазона равноотстающих значений.
     * Например, при start = 1, stop = 2, size = 3 будет получена матрица [[1.0],
     *                                                                     [1.5],
     *                                                                     [2.0]]
     * При start = 1, stop = 2, size = 5 будет получена матрица [[1.0],
     *                                                           [1.25],
     *                                                           [1.5],
     *                                                           [1.75],
     *                                                           [2.0]]
     * @param start начальное значение
     * @param stop конечное значение (включительно)
     * @param size размер диапазона
     * @return вектор-столбец
     */
    public static Matrix getLinSpace(double start, double stop, int size) {
        double[][] result = new double[size][1];
        double step = (stop - start) / (size - 1);
        for (int i = 0; i < size; i++)
            result[i][0] = start + step * i;
        return new Matrix(result);
    }

    /**
     * Получение диапазона равноотстающих целочисленных значений. См. getLinSpace для вещественных аргументов.
     * @param start начальное значение
     * @param stop конечное значение (включительно)
     * @param size размер диапазона
     * @return вектор-столбец
     */
    public static int[] getLinSpace(int start, int stop, int size) {
        int[] result = new int[size];
        double step = (stop - start) * 1.0 / (size - 1);
        for (int i = 0; i < size; i++)
            result[i] = (int)Math.round(start + step * i);
        return result;
    }

    /**
     * Вычисление декартового произведения заданных векторов-столбцов
     * @param matrices векторы-столбцы
     * @return результат
     */
    public static Matrix cartesianProduct(Matrix ... matrices) {
        if (matrices.length == 0)
            return null;
        if (matrices.length == 1)
            return matrices[0];
        Arrays.stream(matrices).forEach(Matrix::assertColMatrix);
        int[] sizes = Arrays.stream(matrices).map(Matrix::getRows).mapToInt(Integer::intValue).toArray();
        int totalRows = reduceIntArrayByMultiplyUpToIndex(sizes, matrices.length);
        return IntStream.range(0, matrices.length)
                .mapToObj(i -> extendAndStack(matrices[i],
                        totalRows / reduceIntArrayByMultiplyUpToIndex(sizes, i + 1),
                        reduceIntArrayByMultiplyUpToIndex(sizes, i)))
                .reduce((m1, m2) -> m1.stack(m2, 0))
                .orElseThrow(() -> new RuntimeException("Ошибка во время вычисления декартового произведения"));
    }

    /**
     * Скалярное умножение двух векторов-столбцов
     * @param m1 вектор-столбец
     * @param m2 вектор-столбец
     * @return скаляр
     */
    public static double mulScalar(Matrix m1, Matrix m2) {
        m1.assertSameShape(m2);
        m1.assertColMatrix();
        m2.assertColMatrix();
        double result = 0.0;
        for (int row = 0; row < m1.getRows(); row++)
            result += m1.getValue(row, 0) * m2.getValue(row, 0);
        return result;
    }

    /**
     * Получение случайной перестановки последовательности целых чисел от 0 до заданного числа (не включительно)
     * @param length размер последовательности
     * @return перестановка
     */
    public static int[] getRandomRangePermutation(int length) {
        int[] array = IntStream.range(0, length).toArray();
        int index, temp;
        Random random = new Random();
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
        return array;
    }

    /**
     * Расширение и вертикальная конкатенация матрицы (конкатенируется расширенная матрица)
     * @param matrix матрица
     * @param extendingFactor множитель расширения
     * @param stacksCount количество операций конкатенации
     * @return обработанная матрица
     */
    private static Matrix extendAndStack(Matrix matrix, int extendingFactor, int stacksCount) {
        Matrix extended = matrix.extend(extendingFactor, 0);
        Matrix result = extended;
        for (int j = 1; j < stacksCount; j++)
            result = result.stack(extended, 1);
        return result;
    }

    /**
     * Перемножение элементов массива до заданного индекса
     * @param values массив
     * @param index индекс
     * @return произведение
     */
    private static int reduceIntArrayByMultiplyUpToIndex(int[] values, int index) {
        int result = 1;
        for (int i = 0; i < index && i < values.length; i++)
            result *= values[i];
        return result;
    }

    /**
     * Получение случайной матрицы с заданными параметрами
     * @param type тип случайного распределения
     * @param rows количество строк
     * @param cols количество столбцов
     * @param location математическое ожидание
     * @param scale множитель или СКО
     * @return случайная матрица
     */
    private static Matrix getRandomMatrix(RandomType type, int rows, int cols, double location, double scale) {
        Random random = new Random();
        double[][] result = new double[rows][cols];
        double randomValue = 0.0;
        for (int row = 0; row < rows; row++)
            for (int col = 0; col < cols; col++) {
                switch (type) {
                    case UNIFORM:
                        randomValue = random.nextDouble();
                        break;
                    case NORMAL:
                        randomValue = random.nextGaussian();
                        break;
                    default:
                        throw new IllegalArgumentException("Недопустимое значение type: " + type);
                }
                result[row][col] = randomValue * scale + location;
            }
        return new Matrix(result);
    }

}
