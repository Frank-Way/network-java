package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

import java.util.HashMap;

public class SimpleMapFieldsClass extends TestSerializableClass {
    // поля с ассоциативными списками
    @YamlField protected HashMap<Integer, Integer> integerIntegerMapField;
    @YamlField protected HashMap<Integer, Double> integerDoubleMapField;
    @YamlField protected HashMap<Integer, Boolean> integerBooleanMapMapField;
    @YamlField protected HashMap<Integer, String> integerStringMapMapField;
    
    @YamlField protected HashMap<Double, Integer> doubleIntegerMapField;
    @YamlField protected HashMap<Double, Double> doubleDoubleMapField;
    @YamlField protected HashMap<Double, Boolean> doubleBooleanMapMapField;
    @YamlField protected HashMap<Double, String> doubleStringMapMapField;
    
    @YamlField protected HashMap<String, Integer> stringIntegerMapField;
    @YamlField protected HashMap<String, Double> stringDoubleMapField;
    @YamlField protected HashMap<String, Boolean> stringBooleanMapMapField;
    @YamlField protected HashMap<String, String> stringStringMapMapField;
    
    @YamlField protected HashMap<Boolean, Integer> booleanIntegerMapField;
    @YamlField protected HashMap<Boolean, Double> booleanDoubleMapField;
    @YamlField protected HashMap<Boolean, Boolean> booleanBooleanMapMapField;
    @YamlField protected HashMap<Boolean, String> booleanStringMapMapField;

    @Override
    public void set(ValuesProvider valuesProvider) {
        int size = getRandomSize();
        integerIntegerMapField = (HashMap<Integer, Integer>) valuesProvider.getMap(valuesProvider.getArrayIntegersBoxed(size), valuesProvider.getArrayIntegersBoxed(size));
        integerDoubleMapField = (HashMap<Integer, Double>) valuesProvider.getMap(valuesProvider.getArrayIntegersBoxed(size), valuesProvider.getArrayDoublesBoxed(size));
        integerBooleanMapMapField = (HashMap<Integer, Boolean>) valuesProvider.getMap(valuesProvider.getArrayIntegersBoxed(size), valuesProvider.getArrayBooleansBoxed(size));
        integerStringMapMapField = (HashMap<Integer, String>) valuesProvider.getMap(valuesProvider.getArrayIntegersBoxed(size), valuesProvider.getArrayStrings(size));

        doubleIntegerMapField = (HashMap<Double, Integer>) valuesProvider.getMap(valuesProvider.getArrayDoublesBoxed(size), valuesProvider.getArrayIntegersBoxed(size));
        doubleDoubleMapField = (HashMap<Double, Double>) valuesProvider.getMap(valuesProvider.getArrayDoublesBoxed(size), valuesProvider.getArrayDoublesBoxed(size));
        doubleBooleanMapMapField = (HashMap<Double, Boolean>) valuesProvider.getMap(valuesProvider.getArrayDoublesBoxed(size), valuesProvider.getArrayBooleansBoxed(size));
        doubleStringMapMapField = (HashMap<Double, String>) valuesProvider.getMap(valuesProvider.getArrayDoublesBoxed(size), valuesProvider.getArrayStrings(size));

        stringIntegerMapField = (HashMap<String, Integer>) valuesProvider.getMap(valuesProvider.getArrayStrings(size), valuesProvider.getArrayIntegersBoxed(size));
        stringDoubleMapField = (HashMap<String, Double>) valuesProvider.getMap(valuesProvider.getArrayStrings(size), valuesProvider.getArrayDoublesBoxed(size));
        stringBooleanMapMapField = (HashMap<String, Boolean>) valuesProvider.getMap(valuesProvider.getArrayStrings(size), valuesProvider.getArrayBooleansBoxed(size));
        stringStringMapMapField = (HashMap<String, String>) valuesProvider.getMap(valuesProvider.getArrayStrings(size), valuesProvider.getArrayStrings(size));

        booleanIntegerMapField = (HashMap<Boolean, Integer>) valuesProvider.getMap(valuesProvider.getArrayBooleansBoxed(size), valuesProvider.getArrayIntegersBoxed(size));
        booleanDoubleMapField = (HashMap<Boolean, Double>) valuesProvider.getMap(valuesProvider.getArrayBooleansBoxed(size), valuesProvider.getArrayDoublesBoxed(size));
        booleanBooleanMapMapField = (HashMap<Boolean, Boolean>) valuesProvider.getMap(valuesProvider.getArrayBooleansBoxed(size), valuesProvider.getArrayBooleansBoxed(size));
        booleanStringMapMapField = (HashMap<Boolean, String>) valuesProvider.getMap(valuesProvider.getArrayBooleansBoxed(size), valuesProvider.getArrayStrings(size));
    }

    @Override
    public String toString() {
        return "SimpleMapFieldsClass{" +
//                "integerIntegerMapField=" + integerIntegerMapField +
//                ", integerDoubleMapField=" + integerDoubleMapField +
//                ", integerBooleanMapMapField=" + integerBooleanMapMapField +
//                ", integerStringMapMapField=" + integerStringMapMapField +
//                ", doubleIntegerMapField=" + doubleIntegerMapField +
//                ", doubleDoubleMapField=" + doubleDoubleMapField +
//                ", doubleBooleanMapMapField=" + doubleBooleanMapMapField +
//                ", doubleStringMapMapField=" + doubleStringMapMapField +
//                ", stringIntegerMapField=" + stringIntegerMapField +
//                ", stringDoubleMapField=" + stringDoubleMapField +
//                ", stringBooleanMapMapField=" + stringBooleanMapMapField +
//                ", stringStringMapMapField=" + stringStringMapMapField +
//                ", booleanIntegerMapField=" + booleanIntegerMapField +
//                ", booleanDoubleMapField=" + booleanDoubleMapField +
//                ", booleanBooleanMapMapField=" + booleanBooleanMapMapField +
//                ", booleanStringMapMapField=" + booleanStringMapMapField +
                '}';
    }
}
