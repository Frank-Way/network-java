package models.trainers;

import com.sun.istack.internal.NotNull;
import models.data.Dataset;
import models.interfaces.Copyable;
import models.networks.Network;
import models.interfaces.Debuggable;
import utils.Errors;
import utils.Utils;

import java.util.*;

/**
 * Результаты обучения. Атрибуты модели
 *  testLossesMap - зависимость потери от эпохи;
 *  {@link Network} - сеть, обеспечившая наименьшую потерю при обучении;
 *  {@link Errors} - ошибки работы сети при прогоне части обучающей выборки для валидации;
 *  {@link models.data.Dataset} - обучающая выборка;
 */
public class FitResults implements Copyable<FitResults>, Debuggable {
    private final Map<Integer, Double> testLossesMap;
    private final Network network;
    private final Errors errors;
    private final Dataset dataset;

    /**
     * Конструктор
     * @param testLossesMap  зависимость потери от эпохи
     * @param bestNetwork  сеть, обеспечившая наименьшую потерю при обучении
     * @param errors  ошибки работы сети при прогоне части обучающей выборки для валидации
     * @param dataset  обучающая выборка
     */
    public FitResults(Map<Integer, Double> testLossesMap, Network bestNetwork, Errors errors, Dataset dataset) {
        this.testLossesMap = testLossesMap;
        this.network = bestNetwork;
        this.errors = errors;
        this.dataset = dataset;
    }

    public Map<Integer, Double> getTestLossesMap() {
        return testLossesMap;
    }

    public Network getNetwork() {
        return network;
    }

    public Errors getErrors() {
        return errors;
    }

    public Dataset getDataset() {
        return dataset;
    }

    public double getMaxAbsoluteError() {
        return errors.getMaxAbsoluteError();
    }

    public double getMaxRelativeError() {
        return errors.getMaxRelativeError();
    }

    public double getMeanAbsoluteError() {
        return errors.getMeanAbsoluteError();
    }

    public double getLossMSE() {
        return errors.getLossMSE();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FitResults that = (FitResults) o;
        return Objects.equals(testLossesMap, that.testLossesMap) &&
               Objects.equals(network, that.network) &&
               Objects.equals(errors, that.errors) &&
               Objects.equals(dataset, that.dataset);
    }

    @Override
    public int hashCode() {
        return Objects.hash(testLossesMap, network, errors, dataset);
    }

    @Override
    public String toString() {
        return "FitResults{" +
                "testLossesMap=" + testLossesMap +
                "bestNetwork=" + network +
                "errors=" + errors +
                "dataset=" + dataset +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "ТекущиеРезультатыОбучения{" +
                "потериПоЭпохам=" + testLossesMap +
                "лучшаяСеть=" + network.toString(debugMode) +
                "ошибки=" + errors.toString(debugMode) +
                '}';
    }

    @Override
    public FitResults copy() {
        return new FitResults(new HashMap<>(testLossesMap), Utils.copyNullable(network),
                Utils.copyNullable(errors), Utils.copyNullable(dataset));
    }
}
