package tests.cases.serialization.yaml.random;

import tests.entities.serialization.*;

public class RandomSerializationWrite extends BaseForSerializationYamlRandom {
    @Override
    protected Object[][] getArgs() {
        return new Object[][] {
                {new SimpleFieldsClass(), getValuesProvider(), false, getSerializer()},
                {new ArrayFieldsClass(), getValuesProvider(), false, getSerializer()},
                {new NestedArrayFieldsClass(), getValuesProvider(), false, getSerializer()},
                {new ListFieldClass(), getValuesProvider(), false, getSerializer()},
                {new NestedListFieldsClass(), getValuesProvider(), false, getSerializer()},
                {new SetFieldsClass(), getValuesProvider(), false, getSerializer()},
                {new NestedSetFieldsClass(), getValuesProvider(), false, getSerializer()},
                {new SimpleMapFieldsClass(), getValuesProvider(), false, getSerializer()},
                {new ComplexMapFieldsClass(), getValuesProvider(), false, getSerializer()},
        };
    }
}
