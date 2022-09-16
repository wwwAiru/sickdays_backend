package ru.egartech.sickday.domain.remain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum FreeSickDayCountType {
    PER_YEAR(4),
    PER_QUARTER(1);

    @Getter
    private final long count;
}
