package utils;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;

import java.util.UUID;

/**
 * Составной идентификатор. Добавление в модель идентификатора родителя позволяет отслеживать цепочки создания объектов.
 * Параметры модели:
 *  uid - собственный идентификатор;
 *  parentUid - идентификатор родителя.
 */
public class MyId implements Copyable<MyId> {
    private final String uid;
    private final String parentUid;

    /**
     * Конструктор
     * @param uid  уникальный идентификатор
     * @param parentUid  идентификатор родителя (при наличии)
     */
    public MyId(String uid, String parentUid) {
        this.uid = uid;
        this.parentUid = parentUid;
    }

    /**
     * Получение идентификатора при наличии родителя
     * @param parentMyId  идентификатор родителя
     * @return  идентификатор
     */
    public static MyId buildNewFromParent(@NotNull MyId parentMyId) {
        return buildNewFromParent(parentMyId);
    }

    public String getUid() {
        return uid;
    }

    public String getParentUid() {
        return parentUid;
    }

    /**
     * Перевод идентификатора к определённому формату для формирования имени потока
     * @return  строка формата [uid]-[parentUid]
     */
    public String toThreadId() {
        return String.format("[%s]-[%s]", uid, parentUid);
    }

    @Override
    public MyId copy() {
        return new MyId(uid, parentUid);
    }

    @Override
    public String toString() {
        return "MyId{" +
                "uid='" + uid + '\'' +
                ", parentUid='" + parentUid + '\'' +
                '}';
    }
}
