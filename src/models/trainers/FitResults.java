package models.trainers;

import models.networks.Network;
import utils.Debuggable;

import java.util.*;

public class FitResults implements Cloneable, Debuggable {
    private Map<Integer, Double> testLossesMap;
    private Network bestNetwork;

    public FitResults() {
        this.testLossesMap = new HashMap<>();
    }

    public Map<Integer, Double> getTestLossesMap() {
        return testLossesMap;
    }

    public Network getBestNetwork() {
        return bestNetwork;
    }

    public void setBestNetwork(Network bestNetwork) {
        this.bestNetwork = bestNetwork;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FitResults that = (FitResults) o;
        return Objects.equals(getTestLossesMap(), that.getTestLossesMap()) && Objects.equals(getBestNetwork(), that.getBestNetwork());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTestLossesMap(), getBestNetwork());
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
    public FitResults clone() {
        try {
            FitResults clone = (FitResults) super.clone();
            clone.testLossesMap = new HashMap<>(testLossesMap);
            clone.bestNetwork = bestNetwork.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
