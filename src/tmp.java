import serialization.exceptions.SerializationException;
import serialization.formatters.Formatter;
import serialization.formatters.yaml.YamlFormatter;
import serialization.wrappers.Wrapper;
import serialization.wrappers.WrapperFactory;
import utils.automatization.ExperimentBuilder;

public class tmp {
    public static void main(String[] args) {
        String doubleFormat = "%13.10f";
        ExperimentBuilder[] experimentBuilders = Experiments.getDefaultExperimentBuilders(doubleFormat);
        Formatter formatter = new YamlFormatter(doubleFormat);
        Wrapper wrapper = WrapperFactory.createWrapper(ExperimentBuilder[].class, formatter);
        try {
            String result = wrapper.writeValue(experimentBuilders);
            System.out.println(result);
        } catch (SerializationException e) {
            e.printStackTrace();
        }
    }
}
