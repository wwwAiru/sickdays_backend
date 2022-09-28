package ru.egartech.sickday.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.egartech.sdk.exception.dto.ApiErrorDto;
import ru.egartech.sdk.exception.handler.AbstractRestExceptionHandler;
import ru.egartech.sdk.util.MessageSourceUtils;
import ru.egartech.sickday.exception.employee.EmployeeBranchNotFoundException;
import ru.egartech.sickday.exception.employee.EmployeeException;
import ru.egartech.sickday.exception.employee.EmployeeNotFoundException;
import ru.egartech.sickday.property.EmployeeMessageProperties;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class EmployeeRestExceptionHandler extends AbstractRestExceptionHandler {
    private final EmployeeMessageProperties messages;

    public EmployeeRestExceptionHandler(MessageSourceUtils messageSourceUtils,
                                        EmployeeMessageProperties messages) {
        super(messageSourceUtils);
        this.messages = messages;
    }

    @ExceptionHandler(EmployeeException.class)
    private ApiErrorDto handleEmployeeException(EmployeeException e) {
        return buildMessage(e);
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    private ApiErrorDto handleEmployeeNotFoundException(EmployeeNotFoundException e, WebRequest req) {
        return buildMessage(messageSourceUtils, e, req, messages.getNotFound());
    }

    @ExceptionHandler(EmployeeBranchNotFoundException.class)
    private ApiErrorDto handleEmployeeBranchNotFoundException(EmployeeBranchNotFoundException e, WebRequest req) {
        return buildMessage(messageSourceUtils, e, req, messages.getNotFoundBranch());
    }
}
