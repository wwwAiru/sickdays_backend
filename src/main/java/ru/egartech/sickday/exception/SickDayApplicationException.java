package ru.egartech.sickday.exception;

import ru.egartech.sdk.exception.ApplicationException;

public class SickDayApplicationException extends ApplicationException {
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
