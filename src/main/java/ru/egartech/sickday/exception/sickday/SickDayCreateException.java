package ru.egartech.sickday.exception.sickday;

public class SickDayCreateException extends SickDayException {
    public SickDayCreateException() {
        super();
    }

    public SickDayCreateException(String message) {
        super(message);
    }

    public SickDayCreateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SickDayCreateException(Throwable cause) {
        super(cause);
    }
}
