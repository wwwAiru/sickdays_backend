package ru.egartech.sickday.domain.branch;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum BranchType {
    MOSCOW("московский"),
    GOMEL("гомельский"),
    MAKHACHKALINSIY("махачкалинский"),
    BRYANSK("брянский"),
    TOMSK("томский"),
    PENZA("пензенский"),
    VLADIMIR("владимирский"),
    SAMARA("самарский"),
    UNKNOWN("неизвестный"),
    ;

    @Getter
    private final String asString;
}
