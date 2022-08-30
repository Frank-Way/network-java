package tests.entities.serialization;

import serialization.annotations.YamlField;
import tests.utils.ValuesProvider;

public class SimpleFieldsClass extends TestSerializableClass {
    // поля с базовыми типами
    @YamlField protected int integerField;
    @YamlField protected Integer integerBoxedField;

    @YamlField protected double doubleField;
    @YamlField protected Double doubleBoxedField;

    @YamlField protected boolean booleanField;
    @YamlField protected Boolean booleanBoxedField;

    @YamlField protected String stringField;

    // поля с кастомными объектами
    @YamlField protected CustomSerializableClass customObjectField;

//    public SimpleFieldsClass(int integerField, Integer integerBoxedField, double doubleField, Double doubleBoxedField, boolean booleanField, Boolean booleanBoxedField, String stringField, CustomSerializableClass customObjectField) {
//        this.integerField = integerField;
//        this.integerBoxedField = integerBoxedField;
//        this.doubleField = doubleField;
//        this.doubleBoxedField = doubleBoxedField;
//        this.booleanField = booleanField;
//        this.booleanBoxedField = booleanBoxedField;
//        this.stringField = stringField;
//        this.customObjectField = customObjectField;
//    }

    @Override
    public void set(ValuesProvider valuesProvider) {
        integerField = valuesProvider.getInteger();
        integerBoxedField = valuesProvider.getIntegerBoxed();
        doubleField = valuesProvider.getDouble();
        doubleBoxedField = valuesProvider.getDoubleBoxed();
        booleanField = valuesProvider.getBoolean();
        booleanBoxedField = valuesProvider.getBooleanBoxed();
        stringField = valuesProvider.getString();
        customObjectField = new CustomSerializableClass();
        customObjectField.set(valuesProvider);
    }

    @Override
    public String toString() {
        return "SimpleFieldsClass{" +
                "integerField=" + integerField +
                ", integerBoxedField=" + integerBoxedField +
                ", doubleField=" + doubleField +
                ", doubleBoxedField=" + doubleBoxedField +
                ", booleanField=" + booleanField +
                ", booleanBoxedField=" + booleanBoxedField +
                ", stringField='" + stringField + '\'' +
                ", customObjectField=" + customObjectField +
                '}';
    }

    //    @YamlField protected CustomNotSerializableClass customObjectFieldNotSerializable;
}
