package ru.egartech.sickday.exception.employee;

import ru.egartech.sickday.exception.SickDayApplicationException;

public class EmployeeException extends SickDayApplicationException {
    public EmployeeException() {
        super();
    }

    public EmployeeException(String message) {
        super(message);
    }

    public EmployeeException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmployeeException(Throwable cause) {
        super(cause);
    }
}
