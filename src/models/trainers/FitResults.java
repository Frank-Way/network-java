package models.trainers;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.networks.Network;
import models.interfaces.Debuggable;

import java.util.*;

public class FitResults implements Copyable<FitResults>, Debuggable {
    private final Map<Integer, Double> testLossesMap;
    private Network bestNetwork;

    public FitResults() {
        this.testLossesMap = new HashMap<>();
    }

    /***
     * copy-constructor
     */
    private FitResults(Map<Integer, Double> testLossesMap, Network bestNetwork) {
        this.testLossesMap = testLossesMap;
        this.bestNetwork = bestNetwork;
    }

    public Map<Integer, Double> getTestLossesMap() {
        return testLossesMap;
    }

    public Network getBestNetwork() {
        return bestNetwork;
    }

    public void setBestNetwork(@NotNull Network bestNetwork) {
        this.bestNetwork = bestNetwork;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FitResults)) return false;
        FitResults that = (FitResults) o;
        return Objects.equals(getTestLossesMap(), that.getTestLossesMap()) &&
               Objects.equals(getBestNetwork(), that.getBestNetwork());
    }

    @Override
    public int hashCode() {
        return Objects.hash(testLossesMap, bestNetwork);
    }

    @Override
    public String toString() {
        return "FitResults{" +
                "testLossesMap=" + testLossesMap +
                "bestNetwork=" + bestNetwork +
                '}';
    }

    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return "ТекущиеРезультатыОбучения{" +
                "потериПоЭпохам=" + testLossesMap +
                "лучшаяСеть=" + bestNetwork +
                '}';
    }

    @Override
    public FitResults copy() {
        return new FitResults(new HashMap<>(testLossesMap), bestNetwork.copy());
    }
}
