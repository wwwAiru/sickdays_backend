package ru.egartech.sickday.exception.sickday;

public class SickDayDateNotFoundException extends SickDayException {
    public SickDayDateNotFoundException() {
        super();
    }

    public SickDayDateNotFoundException(String message) {
        super(message);
    }

    public SickDayDateNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SickDayDateNotFoundException(Throwable cause) {
        super(cause);
    }
}
