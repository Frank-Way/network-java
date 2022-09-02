package utils.automatization;

import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

import java.util.Arrays;
import java.util.Objects;

@YamlSerializable
public class ExperimentBuilder implements DeepCopyable {
    @YamlField protected String description;
    protected RunConfiguration[] runConfigurations;
    @YamlField protected RunConfigurationBuilder[] runConfigurationBuilders;

    public ExperimentBuilder() {
    }

    public ExperimentBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ExperimentBuilder runConfigurations(RunConfiguration[] runConfigurations) {
        this.runConfigurations = runConfigurations;
        return this;
    }

    public ExperimentBuilder runConfigurationBuilders(RunConfigurationBuilder[] runConfigurationBuilders) {
        this.runConfigurationBuilders = runConfigurationBuilders;
        return this;
    }

    public Experiment build() {
        validate();
        prepare();
        return new Experiment(description, runConfigurations);
    }

    protected void validate() {
        if (runConfigurations == null && runConfigurationBuilders == null)
            throw ExceptionUtils.newWrongBuilderException(this.toString());
    }

    protected void prepare() {
        if (runConfigurations == null)
            runConfigurations = Arrays.stream(runConfigurationBuilders)
                    .map(RunConfigurationBuilder::build).toArray(RunConfiguration[]::new);
    }

    @Override
    public String toString() {
        return "ExperimentBuilder{" +
                "description='" + description + '\'' +
                ", runConfigurations=" + Arrays.toString(runConfigurations) +
                ", runConfigurationBuilders=" + Arrays.toString(runConfigurationBuilders) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ExperimentBuilder that = (ExperimentBuilder) o;
        return Objects.equals(description, that.description) && Arrays.equals(runConfigurations, that.runConfigurations) && Arrays.equals(runConfigurationBuilders, that.runConfigurationBuilders);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(description);
        result = 31 * result + Arrays.hashCode(runConfigurations);
        result = 31 * result + Arrays.hashCode(runConfigurationBuilders);
        return result;
    }

    @Override
    public ExperimentBuilder deepCopy() {
        return new ExperimentBuilder()
                .description(description)
                .runConfigurations(runConfigurations == null ? null : Arrays.stream(runConfigurations)
                        .map(RunConfiguration::deepCopy).toArray(RunConfiguration[]::new))
                .runConfigurationBuilders(runConfigurationBuilders == null ? null : Arrays.stream(runConfigurationBuilders)
                        .map(RunConfigurationBuilder::deepCopy).toArray(RunConfigurationBuilder[]::new));
    }
}
