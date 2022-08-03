package models.interfaces;

/**
 * Интерфейс для реализации глубокой копии
 * @param <T> класс, реализующий интерфейс
 */
public interface Copyable<T> {
    public T copy();  // дженерик позволяет возвращать конкретный тип, а не Object
}
