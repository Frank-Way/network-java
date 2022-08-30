package tests.cases.serialization.yaml.random;

import tests.cases.serialization.yaml.BaseForSerializationYaml;
import tests.utils.RandomValuesProvider;
import tests.utils.ValuesProvider;

public abstract class BaseForSerializationYamlRandom extends BaseForSerializationYaml {
    @Override
    protected ValuesProvider getValuesProvider() {
        return new RandomValuesProvider();
    }
}
