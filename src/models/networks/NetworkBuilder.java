package models.networks;

import models.layers.Layer;
import models.losses.Loss;
import serialization.annotations.YamlSerializable;
import utils.copy.DeepCopyable;

import java.io.Serializable;

/**
 * Билдер для {@link Network}
 */
@YamlSerializable
public abstract class NetworkBuilder implements DeepCopyable, Serializable {
    /**
     * Конструктор
     */
    protected NetworkBuilder() {}

    /**
     * Получение сети
     * @return сеть
     */
    public Network build() {
        validate();  // проверка заданных параметров
        return new Network(getLayers(), getLoss());  // получение сети
    }

    /**
     * Проверка параметров реализуется в наследниках
     */
    protected abstract void validate();

    /**
     * Формирование слоёв
     * @return набор слоёв
     */
    protected abstract Layer[] getLayers();

    /**
     * Формирование потери
     * @return потеря
     */
    protected abstract Loss getLoss();

    @Override
    public abstract NetworkBuilder deepCopy();
}
