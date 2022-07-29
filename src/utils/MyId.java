package utils;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;

import java.util.Objects;
import java.util.UUID;

/**
 * Составной идентификатор. Добавление в модель идентификатора родителя позволяет отслеживать цепочки создания объектов.
 * Параметры модели:
 *  uid - собственный идентификатор;
 *  parentUid - идентификатор родителя;
 *  hash - хэш.
 */
public class MyId implements Copyable<MyId> {
    private final String uid;
    private final String parentUid;
    private final String hash;

    /**
     * Конструктор
     * @param uid  уникальный идентификатор
     * @param parentUid  идентификатор родителя (при наличии)
     * @param hash  хэш (при необходимости)
     */
    public MyId(String uid, String parentUid, String hash) {
        this.uid = uid;
        this.parentUid = parentUid;
        this.hash = hash;
    }

    /**
     * Получение идентификатора при наличии родителя
     * @param parentMyId  идентификатор родителя
     * @return  идентификатор
     */
    public static MyId buildNewFromParent(@NotNull MyId parentMyId) {
        return buildNewFromParent(parentMyId, null);
    }

    /**
     * Получение идентификатора с хэшем при наличии родителя
     * @param parentMyId  идентификатор родителя
     * @param hash  хэш
     * @return  идентификатор
     */
    public static MyId buildNewFromParent(@NotNull MyId parentMyId, String hash) {
        return new MyId(UUID.randomUUID().toString(), parentMyId.uid, hash);
    }

    public String getUid() {
        return uid;
    }

    public String getParentUid() {
        return parentUid;
    }

    public String getHash() {
        return hash;
    }

    /**
     * Перевод идентификатора к определённому формату для формирования имени потока
     * @return  строка формата [uid]-[parentUid]-[hash]
     */
    public String toThreadId() {
        return String.format("[%s]-[%s]-[%s]", uid, parentUid, hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyId myId = (MyId) o;
        return Objects.equals(uid, myId.uid) &&
               Objects.equals(parentUid, myId.parentUid) &&
               Objects.equals(hash, myId.hash);
    }

    @Override
    public MyId copy() {
        return new MyId(uid, parentUid, hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, parentUid, hash);
    }

    @Override
    public String toString() {
        return "MyId{" +
                "uid='" + uid + '\'' +
                ", parentUid='" + parentUid + '\'' +
                ", hash='" + hash + '\'' +
                '}';
    }
}
