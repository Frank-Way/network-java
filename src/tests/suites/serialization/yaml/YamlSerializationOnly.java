package tests.suites.serialization.yaml;

import tests.cases.CaseWrapper;
import tests.cases.serialization.yaml.random.RandomSerializationWrite;
import tests.cases.serialization.yaml.random.RandomSerializationWriteRead;
import tests.suites.Suite;

import java.util.Date;

public class YamlSerializationOnly extends Suite {
    @Override
    public CaseWrapper[] getCases() {
        return new CaseWrapper[] {
                new RandomSerializationWrite(),
                new RandomSerializationWriteRead(),
        };
    }

    @Override
    public Object[][] getArgs() {
        return new Object[][] {
                {},
                {},
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
