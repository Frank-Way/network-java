package models.trainers;

import models.data.Dataset;
import models.networks.Network;
import utils.Errors;
import utils.copy.DeepCopyable;

import java.util.HashMap;
import java.util.Map;

/**
 * Результаты обучения. Атрибуты модели
 *  testLossesMap - зависимость потери от эпохи;
 *  {@link Network} - сеть, обеспечившая наименьшую потерю при обучении;
 *  {@link Errors} - ошибки работы сети при прогоне части обучающей выборки для валидации;
 *  {@link models.data.Dataset} - обучающая выборка;
 */
public class FitResults implements DeepCopyable {
    private final Map<Integer, Double> testLossesMap;
    private final Network network;
    private final Errors errors;
    private final Dataset dataset;
    private final long timeSpent;

    /**
     * Конструктор
     * @param testLossesMap  зависимость потери от эпохи
     * @param bestNetwork  сеть, обеспечившая наименьшую потерю при обучении
     * @param errors  ошибки работы сети при прогоне части обучающей выборки для валидации
     * @param dataset  обучающая выборка
     * @param timeSpent  длительность обучения по времени
     */
    public FitResults(Map<Integer, Double> testLossesMap, Network bestNetwork, Errors errors,
                      Dataset dataset, long timeSpent) {
        this.testLossesMap = testLossesMap;
        this.network = bestNetwork;
        this.errors = errors;
        this.dataset = dataset;
        this.timeSpent = timeSpent;
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

    public long getTimeSpent() {
        return timeSpent;
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
    public String toString() {
        return "FitResults{" +
                "testLossesMap=" + testLossesMap +
                ", network=" + network +
                ", errors=" + errors +
                ", dataset=" + dataset +
                ", timeSpent=" + timeSpent +
                '}';
    }

    @Override
    public FitResults deepCopy() {
        return new FitResults(new HashMap<>(testLossesMap), network.deepCopy(),
                errors.deepCopy(), dataset.deepCopy(), timeSpent);
    }
}
