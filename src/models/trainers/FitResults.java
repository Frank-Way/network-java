package models.trainers;

import models.data.Dataset;
import models.networks.Network;
import utils.Errors;
import utils.copy.DeepCopyable;

import java.util.HashMap;
import java.util.Map;

/**
 * Результаты обучения. Атрибуты модели:
 * <pre><ul>
 *  <li>testLossesMap - зависимость потери от эпохи;</li>
 *  <li>{@link Network}             - сеть, обеспечившая наименьшую потерю при обучении;</li>
 *  <li>{@link Errors}              - ошибки работы сети при прогоне части обучающей выборки для валидации;</li>
 *  <li>{@link models.data.Dataset} - обучающая выборка;</li>
 *  <li>timeStart                   - время начала обучения;</li>
 *  <li>timeStop                    - время завершения обучения</li>
 * </ul></pre>
 */
public class FitResults implements DeepCopyable {
    private final Map<Integer, Double> testLossesMap;
    private final Network network;
    private final Errors errors;
    private final Dataset dataset;
    private final long timeStart;
    private final long timeStop;

    /**
     * Конструктор, см. описание параметров в {@link FitResults}
     */
    public FitResults(Map<Integer, Double> testLossesMap, Network network, Errors errors, Dataset dataset,
                      long timeStart, long timeStop) {
        this.testLossesMap = testLossesMap;
        this.network = network;
        this.errors = errors;
        this.dataset = dataset;
        this.timeStart = timeStart;
        this.timeStop = timeStop;
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

    public long getTimeStart() {
        return timeStart;
    }

    public long getTimeStop() {
        return timeStop;
    }

    @Override
    public String toString() {
        return "FitResults{" +
                "testLossesMap=" + testLossesMap +
                ", network=" + network +
                ", errors=" + errors +
                ", dataset=" + dataset +
                ", timeStart=" + timeStart +
                ", timeStop=" + timeStop +
                '}';
    }

    @Override
    public FitResults deepCopy() {
        return new FitResults(new HashMap<>(testLossesMap), network.deepCopy(),
                errors.deepCopy(), dataset.deepCopy(), timeStart, timeStop);
    }
}
