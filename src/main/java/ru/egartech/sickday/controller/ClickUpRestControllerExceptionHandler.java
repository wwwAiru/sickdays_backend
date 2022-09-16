package ru.egartech.sickday.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import ru.egartech.sickday.model.ApiErrorMessageDto;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class ClickUpRestControllerExceptionHandler {
    @ExceptionHandler(HttpClientErrorException.class)
    private ApiErrorMessageDto handleHttpClientErrorException(HttpClientErrorException e) {
        return GlobalRestControllerExceptionHandler
                .buildApiErrorMessageDto(e);
    }
}
