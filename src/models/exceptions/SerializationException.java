package models.exceptions;

import java.io.IOException;

/**
 * Ошибка при сериализации
 */
public class SerializationException extends IOException {
    public SerializationException() {
    }

    public SerializationException(String message) {
        super(message);
    }

    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}
