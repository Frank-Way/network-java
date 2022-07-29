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

public class ApproximationDataLoader extends DataLoader {
    private static final Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    @Override
    public Dataset load(@NotNull LoadParameters parameters) {
        return load((ApproximationLoadParameters) parameters);
    }

    public Dataset load(@NotNull ApproximationLoadParameters parameters) {
        return new Dataset(getExtendedData(parameters.getFunction(), parameters.getSize(), parameters.getExtendingFactor()),
                getData(parameters.getFunction(), parameters.getTestSize()),
                getData(parameters.getFunction(), parameters.getValidSize()));
    }

    private static Data getData(@NotNull Function function, int size) {
        Matrix inputs = getInputs(function, size);
        return new Data(inputs, getOutputs(function, inputs));
    }

    private static Data getExtendedData(@NotNull Function function, int size, double extendingFactor) {
        try {
            Matrix inputs = getInputs(function, size, extendingFactor);
            return new Data(inputs, getOutputs(function, inputs));
        } catch (Exception e) {
            logger.warning(String.format("Ошибка при вычислении функции \"%s\" от аргументов в диапазонах [%s]: %s\n Расширение не будет выполнено",
                    function.getExpression(),
                    function.getVariableRanges().stream()
                            .map(vr -> vr.getExtendedRange(size, extendingFactor)).collect(Collectors.toList()),
                    e.getMessage()));
            Matrix inputs = getInputs(function, size);
            return new Data(inputs, getOutputs(function, inputs));
        }
    }

    private static Matrix getInputs(@NotNull Function function, int size) {
        return getInputs(function, size, 1.0);
    }

    private static Matrix getInputs(@NotNull Function function, int size, double extendingFactor) {
        return MatrixOperations.cartesianProduct(function.getVariableRanges().stream()
                .map(vr -> vr.getExtendedRange(size, extendingFactor)).collect(Collectors.toList()));
    }

    private static Matrix getOutputs(@NotNull Function function, @NotNull Matrix inputs) {
        double[][] result = new double[inputs.getRows()][1];
        for (int row = 0; row < inputs.getRows(); row++)
            result[row][0] = function.calculate(inputs.getValue(row));
        return new Matrix(result);
    }
}
