package models.data.approximation;

import models.data.Data;
import models.data.DataLoader;
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
public class ApproxDataLoader extends DataLoader {
    private static final transient Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public ApproxDataLoader() {}

    /**
     * Получение выборки для обучения
     * @param parameters параметры выборки
     * @return           выборка для обучения
     */
    @Override
    protected Data getTrainData(LoadParameters parameters) {
        if (parameters instanceof ApproxLoadParameters) {
            ApproxLoadParameters approxLoadParameters = (ApproxLoadParameters) parameters;
            return getExtendedData(approxLoadParameters.getFunction(), approxLoadParameters.getSizes(),
                    approxLoadParameters.getExtendingFactors(), approxLoadParameters.getNoiseMode());
        }
        throw new IllegalArgumentException("Не известный класс с параметрами: " + parameters);
    }

    /**
     * Получение тестовой выборки
     * @param parameters параметры выборки
     * @return           тестовая выборка
     */
    @Override
    protected Data getTestData(LoadParameters parameters) {
        if (parameters instanceof ApproxLoadParameters) {
            ApproxLoadParameters approxLoadParameters = (ApproxLoadParameters) parameters;
            return getData(approxLoadParameters.getFunction(), approxLoadParameters.getTestSizes(),
                    approxLoadParameters.getNoiseMode());
        }
        throw new IllegalArgumentException("Не известный класс с параметрами: " + parameters);
    }

    /**
     * Получение валидационной выборки
     * @param parameters параметры выборки
     * @return           валидационная выборка
     */
    @Override
    protected Data getValidData(LoadParameters parameters) {
        if (parameters instanceof ApproxLoadParameters) {
            ApproxLoadParameters approxLoadParameters = (ApproxLoadParameters) parameters;
            return getData(approxLoadParameters.getFunction(), approxLoadParameters.getValidSizes(),
                    approxLoadParameters.getNoiseMode());
        }
        throw new IllegalArgumentException("Не известный класс с параметрами: " + parameters);
    }

    /**
     * Получение выборки
     * @param function  функция
     * @param sizes     размеры выборки
     * @param noiseMode тип зашумления выборки
     * @return          выборка
     */
    private Data getData(Function function, int[] sizes, NoiseMode noiseMode) {
        Matrix inputs = getInputs(function, sizes, noiseMode);
        return new Data(inputs, getOutputs(function, inputs));
    }

    /**
     * Получение расширенной выборки
     * @param function         функция
     * @param sizes            размеры выборки
     * @param extendingFactors коэффициенты расширения
     * @param noiseMode        тип зашумления выборки
     * @return                 расширенная выборка
     */
    private Data getExtendedData(Function function, int[] sizes, double[] extendingFactors, NoiseMode noiseMode) {
        try {  // оборачивание в try-catch, так как в результате расширения может быть получен диапазон,
               // где функция не определена
            final Matrix inputs = getInputs(function, sizes, extendingFactors, noiseMode);
            return new Data(inputs, getOutputs(function, inputs));
        } catch (Exception e) {
            logger.warning(String.format("Ошибка при вычислении функции \"%s\" от аргументов в диапазонах [%s]: %s\n" +
                            " Расширение не будет выполнено",
                    function.getExpression(),
                    IntStream.range(0, function.getInputsCount())
                            .mapToObj(i -> function.getVariableRanges()[i].getExtendedRange(sizes[i], extendingFactors[i]))
                            .collect(Collectors.toList()),
                    e.getMessage()));
            // вычисление без расширения
            final Matrix inputs = getInputs(function, sizes, NoiseMode.NONE);
            return new Data(inputs, getOutputs(function, inputs));
        }
    }

    /**
     * Получение входных значений для выборки без расширения
     * @param function  функция
     * @param sizes     размеры выборки
     * @param noiseMode тип зашумления выборки
     * @return          входы для выборки
     */
    private Matrix getInputs(Function function, int[] sizes, NoiseMode noiseMode) {
        return getInputs(function, sizes, IntStream.range(0, function.getInputsCount()).mapToDouble(i -> 1.0).toArray(),
                noiseMode);
    }

    /**
     * Получение входных значений для выборки с расширением
     * @param function         функция
     * @param sizes            размеры выборки
     * @param extendingFactors коэффициенты для выборки
     * @param noiseMode        тип зашумления выборки
     * @return                 входы для выборки
     */
    private Matrix getInputs(Function function, int[] sizes, double[] extendingFactors, NoiseMode noiseMode) {
        Matrix[] inputs = IntStream.range(0, function.getInputsCount())
                .mapToObj(i -> function.getVariableRanges()[i].getExtendedRange(sizes[i], extendingFactors[i]))
                .toArray(Matrix[]::new);
        if (noiseMode.equals(NoiseMode.EXTENDING))
            inputs = Arrays.stream(inputs).map(input -> input.stack(input.add(MatrixUtils.getNoise(input)), 1))
                    .toArray(Matrix[]::new);
        return MatrixUtils.cartesianProduct(inputs);
    }

    /**
     * Вычисление выходных значений
     * @param function функция
     * @param inputs   входные значения
     * @return         выходные значения
     */
    private Matrix getOutputs(Function function, Matrix inputs) {
        double[][] result = new double[inputs.getRows()][1];
        for (int row = 0; row < inputs.getRows(); row++)
            result[row][0] = function.calculate(inputs.getValue(row));
        return new Matrix(result);
    }

    @Override
    public String toString() {
        return "ApproxDataLoader{}";
    }

    @Override
    public DataLoader deepCopy() {
        return new ApproxDataLoader();
    }
}
