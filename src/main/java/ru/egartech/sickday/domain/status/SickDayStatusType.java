package ru.egartech.sickday.domain.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SickDayStatusType {
    NEW("новый"),
    DEVELOP("в разработке"),
    ;

    @Getter
    private final String asString;
}
