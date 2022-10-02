package ru.egartech.sickday.domain.branch;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum BranchType {
    MOSCOW("московский"),
    GOMEL("гомельский"),
    MAKHACHKALINSKIY("махачкалинский"),
    BRYANSK("брянский"),
    TOMSK("томский"),
    PENZA("пензенский"),
    VLADIMIR("владимирский"),
    SAMARA("самарский"),
    UNKNOWN("неизвестный"),
    ;

    private static final Map<String, BranchType> BY_NAMES;

    static {
        BY_NAMES = Arrays
                .stream(BranchType.values())
                .collect(Collectors.toMap(BranchType::getName, Function.identity()));
    }

    public static BranchType valueOfBranch(@NonNull String name) {
        return BY_NAMES.get(name);
    }

    @Getter
    private final String name;
}
