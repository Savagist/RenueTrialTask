package org.example.exception;

/**
 * Исключение, которое может быть выброшено в случае возникновения ошибок при работе с фильтром.
 */
public class FilterException extends Exception {
    /**
     * Создает новый объект FilterException без дополнительного сообщения об ошибке.
     */
    public FilterException() {
        super();
    }

    /**
     * Создает новый объект FilterException с указанным сообщением об ошибке.
     *
     * @param message Сообщение об ошибке, которое будет передано в качестве причины исключения.
     */
    public FilterException(String message) {
        super(message);
    }
}