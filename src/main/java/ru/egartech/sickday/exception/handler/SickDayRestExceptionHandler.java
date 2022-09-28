package ru.egartech.sickday.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.egartech.sdk.exception.dto.ApiErrorDto;
import ru.egartech.sdk.exception.handler.AbstractRestExceptionHandler;
import ru.egartech.sdk.util.MessageSourceUtils;
import ru.egartech.sickday.exception.SickDayApplicationException;
import ru.egartech.sickday.exception.sickday.*;
import ru.egartech.sickday.property.SickDayMessageProperties;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SickDayRestExceptionHandler extends AbstractRestExceptionHandler {
    private final SickDayMessageProperties messages;

    public SickDayRestExceptionHandler(MessageSourceUtils messageSourceUtils,
                                       SickDayMessageProperties messages) {
        super(messageSourceUtils);
        this.messages = messages;
    }

    @ExceptionHandler(SickDayApplicationException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    private ApiErrorDto handleRuntimeException(SickDayApplicationException e) {
        e.printStackTrace();
        return buildMessage(e);
    }

    @ExceptionHandler(SickDayNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiErrorDto handleSickDayNotFoundException(SickDayNotFoundException e, WebRequest req) {
        e.printStackTrace();
        return buildMessage(messageSourceUtils, e, req, messages.getNotFoundId(), e.getId());
    }

    @ExceptionHandler(SickDayDateNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    private ApiErrorDto handleSickDayCreateException(SickDayDateNotFoundException e, WebRequest req) {
        e.printStackTrace();
        return buildMessage(messageSourceUtils, e, req, messages.getNotFoundDate());
    }

    @ExceptionHandler(SickDayTypeNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    private ApiErrorDto handleSickDayCreateException(SickDayTypeNotFoundException e, WebRequest req) {
        e.printStackTrace();
        return buildMessage(messageSourceUtils, e, req, messages.getNotFoundType());
    }

    @ExceptionHandler(SickDayCreateException.class)
    @ResponseStatus(BAD_REQUEST)
    private ApiErrorDto handleSickDayCreateException(SickDayCreateException e, WebRequest req) {
        e.printStackTrace();
        return buildMessage(messageSourceUtils, e, req, messages.getCreate(), e.getId());
    }

    @ExceptionHandler(SickDayUpdateException.class)
    @ResponseStatus(BAD_REQUEST)
    private ApiErrorDto handleSickDayUpdateException(SickDayUpdateException e, WebRequest req) {
        e.printStackTrace();
        return buildMessage(messageSourceUtils, e, req, messages.getUpdate(), e.getId());
    }
}
