package ru.egartech.sickday.controller;

import org.springframework.core.annotation.Order;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import ru.egartech.sickday.model.ApiErrorMessageDto;
import ru.egartech.sickday.util.MessageSourceUtils;

import java.util.Date;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Order
public class GlobalRestControllerExceptionHandler {

    public static ApiErrorMessageDto buildApiErrorMessageDto(MessageSourceUtils messageSourceUtils,
                                                             RuntimeException e,
                                                             WebRequest webRequest,
                                                             String srcToMessageSource) {
        return ApiErrorMessageDto.builder()
                .milliseconds(String.valueOf(new Date().getTime()))
                .propertyClass(e.getClass().getSimpleName())
                .message(messageSourceUtils.buildMessage(
                        srcToMessageSource,
                        webRequest,
                        e.getClass().getSimpleName())
                )
                .build();
    }

    public static ApiErrorMessageDto buildApiErrorMessageDto(RuntimeException e) {
        return ApiErrorMessageDto.builder()
                .milliseconds(String.valueOf(new Date().getTime()))
                .propertyClass(e.getClass().getSimpleName())
                .message(e.getLocalizedMessage())
                .build();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    private ApiErrorMessageDto handleRuntimeException(RuntimeException e) {
        e.printStackTrace();
        return GlobalRestControllerExceptionHandler.buildApiErrorMessageDto(e);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    private ApiErrorMessageDto handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        e.printStackTrace();
        return GlobalRestControllerExceptionHandler.buildApiErrorMessageDto(e);
    }
}
