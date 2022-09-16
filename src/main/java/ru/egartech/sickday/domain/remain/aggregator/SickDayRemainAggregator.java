package ru.egartech.sickday.domain.remain.aggregator;

import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.model.SickDayTaskDto;

import java.util.List;

public interface SickDayRemainAggregator {
    long aggregate(List<SickDayTaskDto> sickDays);

    FreeSickDayExtraditionType getType();
}
