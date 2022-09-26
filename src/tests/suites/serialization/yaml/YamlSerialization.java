package tests.suites.serialization.yaml;

import serialization.serializers.YamlSerializer;
import tests.cases.CaseWrapper;
import tests.cases.serialization.ModelClassesSerialization;
import tests.cases.serialization.TestClassesSerialization;
import tests.suites.Suite;
import tests.utils.RandomValuesProvider;

public class YamlSerialization extends Suite {
    @Override
    public CaseWrapper[] getCases() {
        return new CaseWrapper[] {
                new TestClassesSerialization(),
                new TestClassesSerialization(),
                new ModelClassesSerialization(),
                new ModelClassesSerialization(),
        };
    }

    @Override
    public Object[][] getArgs() {
        final String doubleFormat = "%13.10f";
        return new Object[][] {
                {new RandomValuesProvider(), new YamlSerializer(doubleFormat), false, },
                {new RandomValuesProvider(), new YamlSerializer(doubleFormat),  true, },
                {new RandomValuesProvider(), new YamlSerializer(doubleFormat),  true, },
                {new RandomValuesProvider(), new YamlSerializer(doubleFormat), false, },
        };
    }

    @Override
    public String getId() {
        return getClass().getCanonicalName();
    }

    @Override
    public String getDescription() {
        return "Проверка YAML-сериализации";
    }
}
