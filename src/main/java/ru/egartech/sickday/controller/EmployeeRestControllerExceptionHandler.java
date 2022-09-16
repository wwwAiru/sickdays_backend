package ru.egartech.sickday.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.egartech.sickday.exception.employee.EmployeeBranchNotFoundException;
import ru.egartech.sickday.exception.employee.EmployeeException;
import ru.egartech.sickday.exception.employee.EmployeeNotFoundException;
import ru.egartech.sickday.model.ApiErrorMessageDto;
import ru.egartech.sickday.util.MessageSourceUtils;

import static ru.egartech.sickday.util.MessageSourceUtils.EmployeesExceptionMessageProperties.BRANCH_NOT_FOUND;
import static ru.egartech.sickday.util.MessageSourceUtils.EmployeesExceptionMessageProperties.NOT_FOUND;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class EmployeeRestControllerExceptionHandler {
    private final MessageSourceUtils messageSourceUtils;

    @ExceptionHandler(EmployeeException.class)
    private ApiErrorMessageDto handleEmployeeException(EmployeeException e) {
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(e);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    private ApiErrorMessageDto handleEmployeeNotFoundException(EmployeeNotFoundException e, WebRequest req) {
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(messageSourceUtils, e, req, NOT_FOUND.getName());
    }

    @ExceptionHandler(EmployeeBranchNotFoundException.class)
    private ApiErrorMessageDto handleEmployeeBranchNotFoundException(EmployeeBranchNotFoundException e,
                                                                     WebRequest req) {
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(messageSourceUtils, e, req, BRANCH_NOT_FOUND.getName());
    }
}
