package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

import java.util.List;

public class NestedListFieldsClass extends TestSerializableClass {
    // поля с вложенными списками
    @YamlField protected List<List<Integer>> integerNestedListW;

    @YamlField protected List<List<Double>> doubleNestedListW;

    @YamlField protected List<List<Boolean>> booleanNestedListW;

    @YamlField protected List<List<String>> stringNestedList;

    @YamlField protected List<List<CustomSerializableClass>> customObjectNestedList;

    @Override
    public void set(ValuesProvider valuesProvider) {
        int size1 = getRandomSize();
        int size2 = getRandomSize();
        integerNestedListW = valuesProvider.getNestedListIntegersBoxed(size1, size2);
        doubleNestedListW = valuesProvider.getNestedListDoublesBoxed(size1, size2);
        booleanNestedListW = valuesProvider.getNestedListBooleansBoxed(size1, size2);
        stringNestedList = valuesProvider.getNestedListStrings(size1, size2);
        customObjectNestedList = CustomSerializableClass.getNestedList(valuesProvider, size1, size2);
    }

    @Override
    public String toString() {
        return "NestedListFieldsClass{" +
//                "integerNestedListW=" + integerNestedListW +
//                ", doubleNestedListW=" + doubleNestedListW +
//                ", booleanNestedListW=" + booleanNestedListW +
//                ", stringNestedList=" + stringNestedList +
//                ", customObjectNestedList=" + customObjectNestedList +
                '}';
    }
}
