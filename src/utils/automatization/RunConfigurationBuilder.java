package utils.automatization;

import models.trainers.FitParameters;
import models.trainers.FitParametersBuilder;
import serialization.annotations.YamlField;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

import java.util.Objects;

@YamlSerializable
public class RunConfigurationBuilder implements DeepCopyable {
    protected FitParameters fitParameters;
    @YamlField protected FitParametersBuilder fitParametersBuilder;
    @YamlField protected int retries;
    @YamlField protected String description;

    public RunConfigurationBuilder() {
    }

    public RunConfigurationBuilder fitParametersBuilder(FitParametersBuilder fitParametersBuilder) {
        this.fitParametersBuilder = fitParametersBuilder;
        return this;
    }

    public RunConfigurationBuilder fitParameters(FitParameters fitParameters) {
        this.fitParameters = fitParameters;
        return this;
    }

    public RunConfigurationBuilder retries(int retries) {
        this.retries = retries;
        return this;
    }

    public RunConfigurationBuilder description(String description) {
        this.description = description;
        return this;
    }

    public RunConfiguration build() {
        validate();
        prepare();
        return new RunConfiguration(retries, description, fitParameters);
    }

    protected void validate() {
        if (fitParameters == null && fitParametersBuilder == null)
            throw ExceptionUtils.newWrongBuilderException(this.toString());
    }

    protected void prepare() {
        if (fitParameters == null)
            fitParameters = fitParametersBuilder.build();
    }

    @Override
    public RunConfigurationBuilder deepCopy() {
        return new RunConfigurationBuilder()
                .fitParameters(fitParameters == null ? null : fitParameters.deepCopy())
                .fitParametersBuilder(fitParametersBuilder == null ? null : fitParametersBuilder.deepCopy())
                .retries(retries)
                .description(description);
    }

    @Override
    public String toString() {
        return "RunConfigurationBuilder{" +
                "fitParameters=" + fitParameters +
                ", fitParametersBuilder=" + fitParametersBuilder +
                ", retries=" + retries +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RunConfigurationBuilder that = (RunConfigurationBuilder) o;
        return retries == that.retries && Objects.equals(fitParameters, that.fitParameters) && Objects.equals(fitParametersBuilder, that.fitParametersBuilder) && Objects.equals(description, that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fitParameters, fitParametersBuilder, retries, description);
    }
}
