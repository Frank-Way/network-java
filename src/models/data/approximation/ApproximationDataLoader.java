package models.data.approximation;

import models.data.Data;
import models.data.DataLoader;
import models.data.Dataset;
import models.data.LoadParameters;
import models.data.approximation.functions.Function;
import models.math.Matrix;
import models.math.MatrixUtils;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Генератор обучающей выборки для задачи аппроксимации функции
 */
public class ApproximationDataLoader extends DataLoader {
    private static final transient Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ApproximationDataLoader() {}
    @Override
    public Dataset load(LoadParameters parameters) {
        return load((ApproximationLoadParameters) parameters);
    }

    @Override
    public DataLoader deepCopy() {
        return new ApproximationDataLoader();
    }

    /**
     * Генерация выборки
     * @param parameters параметры для генерации выборки
     * @return выборка
     */
    public Dataset load(ApproximationLoadParameters parameters) {
        return new Dataset(getExtendedData(parameters.getFunction(), parameters.getSize(), parameters.getExtendingFactors()),
                getData(parameters.getFunction(), parameters.getTestSize()),
                getData(parameters.getFunction(), parameters.getValidSize()));
    }

    /**
     * Получение выборки
     * @param function функция
     * @param size размер выборки
     * @return выборка
     */
    private static Data getData(Function function, int size) {
        Matrix inputs = getInputs(function, size);
        return new Data(inputs, getOutputs(function, inputs));
    }

    /**
     * Получение расширенной выборки
     * @param function функция
     * @param size размер выборки
     * @param extendingFactors коэффициенты расширения
     * @return расширенная выборка
     */
    private static Data getExtendedData(Function function, int size, double[] extendingFactors) {
        try {  // оборачивание в try-catch, так как в результате расширения может быть получен диапазон,
               // где функция не определена
            Matrix inputs = getInputs(function, size, extendingFactors);
            return new Data(inputs, getOutputs(function, inputs));
        } catch (Exception e) {
            logger.warning(String.format("Ошибка при вычислении функции \"%s\" от аргументов в диапазонах [%s]: %s\n" +
                            " Расширение не будет выполнено",
                    function.getExpression(),
                    IntStream.range(0, function.getInputsCount())
                            .mapToObj(i -> function.getVariableRanges()[i].getExtendedRange(size, extendingFactors[i]))
                            .collect(Collectors.toList()),
                    e.getMessage()));
            // вычисление без расширения
            Matrix inputs = getInputs(function, size);
            return new Data(inputs, getOutputs(function, inputs));
        }
    }

    /**
     * Получение входных значений для выборки без расширения
     * @param function функция
     * @param size размер выборки
     * @return входы для выборки
     */
    private static Matrix getInputs(Function function, int size) {
        return getInputs(function, size, IntStream.range(0, function.getInputsCount()).mapToDouble(i -> 1.0).toArray());
    }

    /**
     * Получение входных значений для выборки с расширением
     * @param function функция
     * @param size размер выборки
     * @param extendingFactors коэффициенты для выборки
     * @return входы для выборки
     */
    private static Matrix getInputs(Function function, int size, double[] extendingFactors) {
        Matrix[] inputs = IntStream.range(0, function.getInputsCount())
                .mapToObj(i -> function.getVariableRanges()[i].getExtendedRange(size, extendingFactors[i]))
                .toArray(Matrix[]::new);
        return MatrixUtils.cartesianProduct(inputs);
    }

    /**
     * Вычисление выходных значений
     * @param function функция
     * @param inputs входные значения
     * @return выходные значения
     */
    private static Matrix getOutputs(Function function, Matrix inputs) {
        double[][] result = new double[inputs.getRows()][1];
        for (int row = 0; row < inputs.getRows(); row++)
            result[row][0] = function.calculate(inputs.getValue(row));
        return new Matrix(result);
    }

    @Override
    public String toString() {
        return "ApproximationDataLoader{}";
    }
}
