package models.interfaces;

/**
 * Утилитарный интерфейс для сущности, имеющей "debug" и "обычный" методы toString
 */
public interface Debuggable {
    String toString(boolean debugMode);
}
