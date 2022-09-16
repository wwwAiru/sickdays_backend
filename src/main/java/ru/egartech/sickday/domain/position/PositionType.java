package ru.egartech.sickday.domain.position;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public enum PositionType {
    DEVELOPER("разработчик"),
    TESTER("тестировщик"),
    ANALYTIC("аналитик"),
    ;

    private static final Map<String, PositionType> BY_POSITION = new HashMap<>();

    static {
        BY_POSITION.put(DEVELOPER.getName(), DEVELOPER);
        BY_POSITION.put(TESTER.getName(), TESTER);
        BY_POSITION.put(ANALYTIC.getName(), ANALYTIC);
    }

    @Getter
    private final String name;

    public static PositionType valueOfPosition(String string) {
        return BY_POSITION.get(string);
    }
}
