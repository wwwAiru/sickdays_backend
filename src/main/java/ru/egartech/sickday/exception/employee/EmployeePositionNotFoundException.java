package ru.egartech.sickday.exception.employee;

public class EmployeePositionNotFoundException extends EmployeeNotFoundException {
    public EmployeePositionNotFoundException() {
        super();
    }

    public EmployeePositionNotFoundException(String message) {
        super(message);
    }

    public EmployeePositionNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmployeePositionNotFoundException(Throwable cause) {
        super(cause);
    }
}
