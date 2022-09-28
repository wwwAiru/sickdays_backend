package ru.egartech.sickday.exception.sickday;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.egartech.sickday.exception.SickDayApplicationException;

@Data
@EqualsAndHashCode(callSuper = true)
public class SickDayException extends SickDayApplicationException {
    protected String id;

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
