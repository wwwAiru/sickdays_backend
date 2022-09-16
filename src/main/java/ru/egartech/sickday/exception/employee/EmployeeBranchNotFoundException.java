package ru.egartech.sickday.exception.employee;

public class EmployeeBranchNotFoundException extends EmployeeNotFoundException {
    public EmployeeBranchNotFoundException() {
        super();
    }

    public EmployeeBranchNotFoundException(String message) {
        super(message);
    }

    public EmployeeBranchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmployeeBranchNotFoundException(Throwable cause) {
        super(cause);
    }
}
