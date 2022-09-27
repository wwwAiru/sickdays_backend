package ru.egartech.sickday.domain.position;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum PositionType {
    DEVELOPER("разработчик"),
    TESTER("тестировщик"),
    ANALYTIC("аналитик"),
    ;

    private static final Map<String, PositionType> BY_POSITION;

    static {
        BY_POSITION = Arrays
                .stream(PositionType.values())
                .collect(Collectors.toMap(PositionType::getName, Function.identity()));
    }

    @Getter
    private final String name;

    public static PositionType valueOfPosition(String string) {
        return BY_POSITION.get(string);
    }
}
