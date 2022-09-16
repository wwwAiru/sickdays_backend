package ru.egartech.sickday.exception;

public class SickDayApplicationException extends RuntimeException {
    public SickDayApplicationException() {
        super();
    }

    public SickDayApplicationException(String message) {
        super(message);
    }

    public SickDayApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    public SickDayApplicationException(Throwable cause) {
        super(cause);
    }
}
