package ru.egartech.sickday.exception.sickday;

public class SickDayTypeNotFoundException extends SickDayNotFoundException {
    public SickDayTypeNotFoundException() {
        super();
    }

    public SickDayTypeNotFoundException(String message) {
        super(message);
    }

    public SickDayTypeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SickDayTypeNotFoundException(Throwable cause) {
        super(cause);
    }
}
