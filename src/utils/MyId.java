package utils;

import com.sun.istack.internal.NotNull;
import models.interfaces.Copyable;

import java.util.Objects;
import java.util.UUID;

public class MyId implements Copyable<MyId> {
    private final String uid;
    private final String parentUid;
    private final String hash;

    public MyId(String uid, String parentUid, String hash) {
        this.uid = uid;
        this.parentUid = parentUid;
        this.hash = hash;
    }

    public static MyId buildNewFromParent(@NotNull MyId parentMyId) {
        return buildNewFromParent(parentMyId, null);
    }

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

    public String toThreadId() {
        return String.format("[%s]-[%s]-[%s]", uid, parentUid, hash);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyId myId = (MyId) o;
        return Objects.equals(getUid(), myId.getUid()) &&
               Objects.equals(getParentUid(), myId.getParentUid()) &&
               Objects.equals(getHash(), myId.getHash());
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
