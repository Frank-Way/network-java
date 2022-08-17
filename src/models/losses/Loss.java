package models.losses;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;
import models.interfaces.Debuggable;
import models.math.Matrix;
import serialization.YamlSerializationOptions;
import serialization.YamlSerializationUtils;

import java.io.Serializable;

import static serialization.YamlSerializationOptions.CRLF;

/**
 * Потеря. Позволяет оценить точность работы сети. Атрибуты модели:
 *  prediction - результат вычислений сети;
 *  target - требуемые выходы сети (из обучающей выборки);
 *  output - потеря (оценка);
 *  inputGradient - градиент на входе потери
 */
public abstract class Loss implements Copyable<Loss>, Debuggable, Serializable {
    private static final long serialVersionUID = -4963149158685226973L;
    protected transient Matrix prediction;
    protected transient Matrix target;
    protected transient double output;
    protected transient Matrix inputGradient;

    public Loss() {}

    /***
     * copy-constructor
     */
    protected Loss(Matrix prediction, Matrix target, double output, Matrix inputGradient) {
        this.prediction = prediction;
        this.target = target;
        this.output = output;
        this.inputGradient = inputGradient;
    }

    /**
     * Прямой проход (вычисление потери)
     * @param prediction результаты сети
     * @param target требуемые выходы
     * @return потеря
     */
    public double forward(@NotNull Matrix prediction, @NotNull Matrix target) {
        prediction.assertSameShape(target);
        // вычисления производятся с копиями
        this.prediction = prediction.copy();
        this.target = target.copy();

        output = computeOutput(this.prediction, this.target);

        return output;
    }

    /**
     * Обратный проход (вычисление градиентов)
     * @return градиент на входе
     */
    public Matrix backward() {
        inputGradient = computeInputGradient(prediction, target);
        prediction.assertSameShape(inputGradient);

        return inputGradient;
    }

    /**
     * Логика вычислений определяется наследниками
     */
    protected abstract double computeOutput(@NotNull Matrix prediction, @NotNull Matrix target);

    /**
     * Логика вычислений определяется наследниками
     */
    protected abstract Matrix computeInputGradient(@NotNull Matrix prediction, @NotNull Matrix target);

    @Override
    public abstract Loss copy();

    @Override
    public String toString() {
        return getDebugClassName() + "{" +
                "prediction=" + prediction +
                ", target=" + target +
                ", output=" + output +
                ", inputGradient=" + inputGradient +
                '}';
    }

    @Override
    public String toString(boolean debugMode) {
        if (debugMode)
            return toString();
        return getClassName() + "{" +
                '}';
    }

    protected abstract String getClassName();

    protected abstract String getDebugClassName();

    public String toYaml(int baseIndent, String doubleFormat) {
        StringBuilder sb = new StringBuilder();
        final String baseIndentString = YamlSerializationUtils.repeat(" ", baseIndent);
        sb.append(baseIndentString).append("class: ").append(this.getClass().getCanonicalName()).append(CRLF);
        return sb.toString();
    }

    public static Loss fromYaml(String yaml, int baseIndent) {
        String[] lines = YamlSerializationUtils.removeFirstCharacters(yaml.split(CRLF), yaml.indexOf('c'));
        String cls = YamlSerializationUtils.getClassAsString(lines, 0);
        final String pattern = "(\\" + YamlSerializationOptions.YAML_LIST_PREFIX + ")?class: " + cls.replace(".", "\\.") + "\\n?";
        if (lines.length != 1 || !lines[0].matches(pattern))
            throw new IllegalArgumentException("Не верный формат строки: " + yaml);
        return createLoss(cls);
    }

    protected static Loss createLoss(String cls) {
        if (cls.equals(MeanSquaredError.class.getCanonicalName()))
            return new MeanSquaredError();
        else
            throw new IllegalArgumentException("Не известный класс: " + cls);
    }
}
