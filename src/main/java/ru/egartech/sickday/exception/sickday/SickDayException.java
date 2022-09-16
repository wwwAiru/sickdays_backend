package ru.egartech.sickday.exception.sickday;

import ru.egartech.sickday.exception.SickDayApplicationException;

public class SickDayException extends SickDayApplicationException {
    public SickDayException() {
        super();
    }

    public SickDayException(String message) {
        super(message);
    }

    public SickDayException(String message, Throwable cause) {
        super(message, cause);
    }

    public SickDayException(Throwable cause) {
        super(cause);
    }
}
