package models.losses;

import models.math.Matrix;
import serialization.annotations.YamlSerializable;
import utils.ExceptionUtils;
import utils.copy.DeepCopyable;

import java.io.Serializable;
import java.util.Objects;

/**
 * Потеря. Позволяет оценить точность работы сети. Атрибуты модели:
 * <pre><ul>
 *  <li>prediction    - результат вычислений сети;</li>
 *  <li>target        - требуемые выходы сети (из обучающей выборки);</li>
 *  <li>output        - потеря (оценка);</li>
 *  <li>inputGradient - градиент на входе потери</li>
 * </ul></pre>
 */
@YamlSerializable
public abstract class Loss implements DeepCopyable, Serializable {
    protected transient Matrix prediction;
    protected transient Matrix target;
    protected transient double output;
    protected transient Matrix inputGradient;

    /**
     * Конструктор
     */
    public Loss() {}

    /**
     * Конструктор для создания глубокой копии экземпляра
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
     * @param target     требуемые выходы
     * @return           потеря
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

    /**
     * Создание потери. Позволяет реализовать копирование слоя в абстрактном классе без дублирования кода в наследниках
     * @param clazz         тип потери (наследник Loss)
     * @param prediction    выходное значение сети
     * @param target        требуемое выходное значение сети
     * @param output        выходное значение потери
     * @param inputGradient градиент на входе
     * @return              потеря с указанными параметрами
     */
    protected static Loss createLoss(Class<? extends Loss> clazz, Matrix prediction, Matrix target,
                                     double output, Matrix inputGradient) {
        if (clazz.equals(MeanSquaredError.class))
            return new MeanSquaredError(prediction, target, output, inputGradient);
        throw ExceptionUtils.newUnknownClassException(clazz);
    }
}
