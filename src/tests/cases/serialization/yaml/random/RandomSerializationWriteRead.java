package tests.cases.serialization.yaml.random;

import tests.entities.serialization.*;

public class RandomSerializationWriteRead extends BaseForSerializationYamlRandom {
    @Override
    protected Object[][] getArgs() {
        return new Object[][] {
                {new SimpleFieldsClass(), getValuesProvider(), true, getSerializer()},
                {new ArrayFieldsClass(), getValuesProvider(), true, getSerializer()},
                {new NestedArrayFieldsClass(), getValuesProvider(), true, getSerializer()},
                {new ListFieldClass(), getValuesProvider(), true, getSerializer()},
                {new NestedListFieldsClass(), getValuesProvider(), true, getSerializer()},
                {new SetFieldsClass(), getValuesProvider(), true, getSerializer()},
                {new NestedSetFieldsClass(), getValuesProvider(), true, getSerializer()},
                {new SimpleMapFieldsClass(), getValuesProvider(), true, getSerializer()},
                {new ComplexMapFieldsClass(), getValuesProvider(), true, getSerializer()},
        };
    }
}
