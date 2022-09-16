package ru.egartech.sickday.domain.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public enum SickDayType {
    SICK_TIME(0, "больничный"),
    SICK_DAY(1, "sick day"),
    ;

    private static final Map<String, SickDayType> BY_LABELS = new HashMap<>();

    static {
        Arrays.asList(SickDayType.values()).forEach(e -> BY_LABELS.put(e.getAsString(), e));
    }

    @Getter
    private final Integer orderindex;

    @Getter
    private final String asString;

    public static SickDayType valueOfLabel(String string) {
        return BY_LABELS.get(string);
    }
}
