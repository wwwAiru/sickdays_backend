package ru.egartech.sickday.exception.sickday;

public class SickDayUpdateException extends SickDayException {
    public SickDayUpdateException() {
        super();
    }

    public SickDayUpdateException(String message) {
        super(message);
    }

    public SickDayUpdateException(String message, Throwable cause) {
        super(message, cause);
    }

    public SickDayUpdateException(Throwable cause) {
        super(cause);
    }
}
