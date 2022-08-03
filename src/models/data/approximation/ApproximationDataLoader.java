package models.data.approximation;

import com.sun.istack.internal.NotNull;
import models.data.Data;
import models.data.DataLoader;
import models.data.Dataset;
import models.data.LoadParameters;
import models.data.approximation.functions.Function;
import models.math.Matrix;
import models.math.MatrixOperations;

import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Генератор обучающей выборки для задачи аппроксимации функции
 */
public class ApproximationDataLoader extends DataLoader {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    @Override
    public Dataset load(@NotNull LoadParameters parameters) {
        return load((ApproximationLoadParameters) parameters);
    }

    /**
     * Генерация выборки
     * @param parameters параметры для генерации выборки
     * @return выборка
     */
    public Dataset load(@NotNull ApproximationLoadParameters parameters) {
        return new Dataset(getExtendedData(parameters.getFunction(), parameters.getSize(), parameters.getExtendingFactor()),
                getData(parameters.getFunction(), parameters.getTestSize()),
                getData(parameters.getFunction(), parameters.getValidSize()));
    }

    /**
     * Получение выборки
     * @param function функция
     * @param size размер выборки
     * @return выборка
     */
    private static Data getData(@NotNull Function function, int size) {
        Matrix inputs = getInputs(function, size);
        return new Data(inputs, getOutputs(function, inputs));
    }

    /**
     * Получение расширенной выборки
     * @param function функция
     * @param size размер выборки
     * @param extendingFactor коэффициент расширения
     * @return расширенная выборка
     */
    private static Data getExtendedData(@NotNull Function function, int size, double extendingFactor) {
        try {  // оборачивание в try-catch, так как в результате расширения может быть получен диапазон,
               // где функция не определена
            Matrix inputs = getInputs(function, size, extendingFactor);
            return new Data(inputs, getOutputs(function, inputs));
        } catch (Exception e) {
            logger.warning(String.format("Ошибка при вычислении функции \"%s\" от аргументов в диапазонах [%s]: %s\n" +
                            " Расширение не будет выполнено",
                    function.getExpression(),
                    function.getVariableRanges().stream()
                            .map(vr -> vr.getExtendedRange(size, extendingFactor)).collect(Collectors.toList()),
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
    private static Matrix getInputs(@NotNull Function function, int size) {
        return getInputs(function, size, 1.0);
    }

    /**
     * Получение входных значений для выборки с расширением
     * @param function функция
     * @param size размер выборки
     * @param extendingFactor коэффициент для выборки
     * @return входы для выборки
     */
    private static Matrix getInputs(@NotNull Function function, int size, double extendingFactor) {
        return MatrixOperations.cartesianProduct(function.getVariableRanges().stream()
                .map(vr -> vr.getExtendedRange(size, extendingFactor)).collect(Collectors.toList()));
    }

    /**
     * Вычисление выходных значений
     * @param function функция
     * @param inputs входные значения
     * @return выходные значения
     */
    private static Matrix getOutputs(@NotNull Function function, @NotNull Matrix inputs) {
        double[][] result = new double[inputs.getRows()][1];
        for (int row = 0; row < inputs.getRows(); row++)
            result[row][0] = function.calculate(inputs.getValue(row));
        return new Matrix(result);
    }
}
