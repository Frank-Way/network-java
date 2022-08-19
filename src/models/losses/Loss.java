package models.losses;

import models.math.Matrix;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.CopyUtils;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Потеря. Позволяет оценить точность работы сети. Атрибуты модели:
 *  prediction - результат вычислений сети;
 *  target - требуемые выходы сети (из обучающей выборки);
 *  output - потеря (оценка);
 *  inputGradient - градиент на входе потери
 */
@YamlSerializable
public abstract class Loss implements DeepCopyable, Serializable {
//    private static final transient long serialVersionUID = -4963149158685226973L;
    protected transient Matrix prediction;
    protected transient Matrix target;
    protected transient double output;
    protected transient Matrix inputGradient;

    public Loss() {}

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
    public double forward(Matrix prediction, Matrix target) {
        prediction.assertSameShape(target);
        // вычисления производятся с копиями
        this.prediction = prediction.deepCopy();
        this.target = target.deepCopy();

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
    protected abstract double computeOutput(Matrix prediction, Matrix target);

    /**
     * Логика вычислений определяется наследниками
     */
    protected abstract Matrix computeInputGradient(Matrix prediction, Matrix target);

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "prediction=" + prediction +
                ", target=" + target +
                ", output=" + output +
                ", inputGradient=" + inputGradient +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Loss loss = (Loss) o;
        return Double.compare(loss.output, output) == 0 && Objects.equals(prediction, loss.prediction) &&
                Objects.equals(target, loss.target) && Objects.equals(inputGradient, loss.inputGradient);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prediction, target, output, inputGradient);
    }

    @Override
    public Loss deepCopy() {
        return createLoss(getClass(), prediction == null ? null : prediction.deepCopy(),
                target == null ? null : target.deepCopy(),
                output, inputGradient == null ? null : inputGradient.deepCopy());
    }

    protected static Loss createLoss(Class<? extends Loss> clazz, Matrix prediction, Matrix target,
                                     double output, Matrix inputGradient) {
        if (clazz.equals(MeanSquaredError.class))
            return new MeanSquaredError(prediction, target, output, inputGradient);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }
}
