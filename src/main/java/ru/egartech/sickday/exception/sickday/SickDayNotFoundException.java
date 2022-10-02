package ru.egartech.sickday.exception.sickday;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SickDayNotFoundException extends SickDayException {

    public SickDayNotFoundException() {
        super();
    }

    public SickDayNotFoundException(String message) {
        super(message);
    }

    public SickDayNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SickDayNotFoundException(Throwable cause) {
        super(cause);
    }
}
