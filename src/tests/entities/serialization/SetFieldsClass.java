package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

import java.util.HashSet;

public class SetFieldsClass extends TestSerializableClass {
    // поля с множествами
    @YamlField protected HashSet<Integer> integerSetBoxedField;

    @YamlField protected HashSet<Double> doubleSetBoxedField;

    @YamlField protected HashSet<Boolean> booleanSetBoxedField;

    @YamlField protected HashSet<String> stringSetField;

    @YamlField protected HashSet<CustomSerializableClass> customObjectSetField;
//    @YamlField protected HashSet<CustomNotSerializableClass> customObjectSetFieldNotSerializable;


    @Override
    public void set(ValuesProvider valuesProvider) {
        int size = getRandomSize();
        integerSetBoxedField = (HashSet<Integer>) valuesProvider.getSetIntegersBoxed(size);
        doubleSetBoxedField = (HashSet<Double>) valuesProvider.getSetDoublesBoxed(size);
        booleanSetBoxedField = (HashSet<Boolean>) valuesProvider.getSetBooleansBoxed(size);
        stringSetField = (HashSet<String>) valuesProvider.getSetStrings(size);
        customObjectSetField = (HashSet<CustomSerializableClass>) CustomSerializableClass.getSet(valuesProvider, size);
    }

    @Override
    public String toString() {
        return "SetFieldsClass{" +
//                "integerSetBoxedField=" + integerSetBoxedField +
//                ", doubleSetBoxedField=" + doubleSetBoxedField +
//                ", booleanSetBoxedField=" + booleanSetBoxedField +
//                ", stringSetField=" + stringSetField +
//                ", customObjectSetField=" + customObjectSetField +
                '}';
    }
}
