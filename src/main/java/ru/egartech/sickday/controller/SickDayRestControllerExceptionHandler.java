package ru.egartech.sickday.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.egartech.sickday.exception.SickDayApplicationException;
import ru.egartech.sickday.exception.sickday.*;
import ru.egartech.sickday.model.ApiErrorMessageDto;
import ru.egartech.sickday.util.MessageSourceUtils;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static ru.egartech.sickday.util.MessageSourceUtils.SickDaysExceptionMessageProperties.*;

@RestControllerAdvice
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SickDayRestControllerExceptionHandler {
    private final MessageSourceUtils messageSourceUtils;

    @ExceptionHandler(SickDayApplicationException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    private ApiErrorMessageDto handleRuntimeException(SickDayApplicationException e) {
        e.printStackTrace();
        return GlobalRestControllerExceptionHandler.buildApiErrorMessageDto(e);
    }

    @ExceptionHandler(SickDayNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    private ApiErrorMessageDto handleSickDayNotFoundException(SickDayNotFoundException e, WebRequest req) {
        e.printStackTrace();
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(messageSourceUtils, e, req, NOT_FOUND.getName());
    }

    @ExceptionHandler(SickDayDateNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    private ApiErrorMessageDto handleSickDayCreateException(SickDayDateNotFoundException e, WebRequest req) {
        e.printStackTrace();
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(messageSourceUtils, e, req, DATE_NOT_FOUND.getName());
    }

    @ExceptionHandler(SickDayTypeNotFoundException.class)
    @ResponseStatus(BAD_REQUEST)
    private ApiErrorMessageDto handleSickDayCreateException(SickDayTypeNotFoundException e, WebRequest req) {
        e.printStackTrace();
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(messageSourceUtils, e, req, TYPE_NOT_FOUND.getName());
    }

    @ExceptionHandler(SickDayStatusNotFoundException.class)
    private ApiErrorMessageDto handleSickDayStatusNotFoundException(SickDayStatusNotFoundException e,
                                                                    WebRequest req) {
        e.printStackTrace();
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(messageSourceUtils, e, req, STATUS_NOT_FOUND.getName());
    }

    @ExceptionHandler(SickDayCreateException.class)
    @ResponseStatus(BAD_REQUEST)
    private ApiErrorMessageDto handleSickDayCreateException(SickDayCreateException e, WebRequest req) {
        e.printStackTrace();
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(messageSourceUtils, e, req, CREATE.getName());
    }
}
