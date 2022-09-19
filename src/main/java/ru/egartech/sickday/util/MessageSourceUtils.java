package ru.egartech.sickday.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

@Component
@RequiredArgsConstructor
public final class MessageSourceUtils {

    private final MessageSource messageSource;

    public String buildMessage(String code,
                               WebRequest request,
                               Object... args) {
        return messageSource.getMessage(code, args, request.getLocale());
    }

    @RequiredArgsConstructor
    public enum SickDaysExceptionMessageProperties {
        BASE("sick-days.exception"),
        NOT_FOUND(BASE.getName().concat(".not-found-id")),
        CREATE(BASE.getName().concat(".create")),
        DATE_NOT_FOUND(BASE.getName().concat(".not-found-date")),
        TYPE_NOT_FOUND(BASE.getName().concat(".not-found-type")),
        STATUS_NOT_FOUND(BASE.getName().concat(".not-found-status")),
        ;

        @Getter
        private final String name;
    }

    @RequiredArgsConstructor
    public enum EmployeesExceptionMessageProperties {
        BASE("employees.exception"),
        NOT_FOUND(BASE.getName().concat(".not-found")),
        BRANCH_NOT_FOUND(BASE.getName().concat(".not-found-branch")),
        ;

        @Getter
        private final String name;
    }

}
