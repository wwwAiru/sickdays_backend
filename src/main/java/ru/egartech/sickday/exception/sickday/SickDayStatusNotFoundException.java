package ru.egartech.sickday.exception.sickday;

public class SickDayStatusNotFoundException extends SickDayNotFoundException {
    public SickDayStatusNotFoundException() {
        super();
    }

    public SickDayStatusNotFoundException(String message) {
        super(message);
    }

    public SickDayStatusNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SickDayStatusNotFoundException(Throwable cause) {
        super(cause);
    }
}
