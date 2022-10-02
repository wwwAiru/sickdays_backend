package ru.egartech.sickday.domain.status;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SickDayTaskStatusType {
    NEW("новый"),
    DEVELOP("в работе");

    @Getter
    private final String asString;
}
