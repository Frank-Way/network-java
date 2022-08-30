package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

import java.util.Set;

public class NestedSetFieldsClass extends TestSerializableClass {
    // поля с вложенными множествами
    @YamlField protected Set<Set<Integer>> integerNestedSetBoxedField;

    @YamlField protected Set<Set<Double>> doubleNestedSetBoxedField;

    @YamlField protected Set<Set<Boolean>> booleanNestedSetBoxedField;

    @YamlField protected Set<Set<String>> stringNestedSetField;

    @YamlField protected Set<Set<CustomSerializableClass>> customObjectNestedSetField;

    @Override
    public void set(ValuesProvider valuesProvider) {
        int size1 = getRandomSize();
        int size2 = getRandomSize();
        integerNestedSetBoxedField = valuesProvider.getNestedSetIntegersBoxed(size1, size2);
        doubleNestedSetBoxedField = valuesProvider.getNestedSetDoublesBoxed(size1, size2);
        booleanNestedSetBoxedField = valuesProvider.getNestedSetBooleansBoxed(size1, size2);
        stringNestedSetField = valuesProvider.getNestedSetStrings(size1, size2);
        customObjectNestedSetField = CustomSerializableClass.getNestedSet(valuesProvider, size1, size2);
    }

    @Override
    public String toString() {
        return "NestedSetFieldsClass{" +
//                "integerNestedSetBoxedField=" + integerNestedSetBoxedField +
//                ", doubleNestedSetBoxedField=" + doubleNestedSetBoxedField +
//                ", booleanNestedSetBoxedField=" + booleanNestedSetBoxedField +
//                ", stringNestedSetField=" + stringNestedSetField +
//                ", customObjectNestedSetField=" + customObjectNestedSetField +
                '}';
    }
}
