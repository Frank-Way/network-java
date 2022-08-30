package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

import java.util.ArrayList;

public class ListFieldClass extends TestSerializableClass {
    // поля со списками
    @YamlField protected ArrayList<Integer> integerListBoxedField;

    @YamlField protected ArrayList<Double> doubleListBoxedField;

    @YamlField protected ArrayList<Boolean> booleanListBoxedField;

    @YamlField protected ArrayList<String> stringListField;

    @YamlField
    protected ArrayList<CustomSerializableClass> customObjectListField;
//    @YamlField protected ArrayList<CustomNotSerializableClass> customObjectListFieldNotSerializable;

//    public ListFieldClass(ArrayList<Integer> integerListBoxedField, ArrayList<Double> doubleListBoxedField, ArrayList<Boolean> booleanListBoxedField, ArrayList<String> stringListField, ArrayList<CustomSerializableClass> customObjectListField) {
//        this.integerListBoxedField = integerListBoxedField;
//        this.doubleListBoxedField = doubleListBoxedField;
//        this.booleanListBoxedField = booleanListBoxedField;
//        this.stringListField = stringListField;
//        this.customObjectListField = customObjectListField;
//    }

    @Override
    public void set(ValuesProvider valuesProvider) {
        int size = getRandomSize();
        integerListBoxedField = (ArrayList<Integer>) valuesProvider.getListIntegersBoxed(size);
        doubleListBoxedField = (ArrayList<Double>) valuesProvider.getListDoublesBoxed(size);
        booleanListBoxedField = (ArrayList<Boolean>) valuesProvider.getListBooleansBoxed(size);
        stringListField = (ArrayList<String>) valuesProvider.getListStrings(size);
        customObjectListField = (ArrayList<CustomSerializableClass>) CustomSerializableClass.getList(valuesProvider, size);
    }

    @Override
    public String toString() {
        return "ListFieldClass{" +
//                "integerListBoxedField=" + integerListBoxedField +
//                ", doubleListBoxedField=" + doubleListBoxedField +
//                ", booleanListBoxedField=" + booleanListBoxedField +
//                ", stringListField=" + stringListField +
//                ", customObjectListField=" + customObjectListField +
                '}';
    }
}
