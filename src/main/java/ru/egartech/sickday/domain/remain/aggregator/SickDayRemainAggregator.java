package ru.egartech.sickday.domain.remain.aggregator;

import lombok.NonNull;
import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.model.SickDayTaskDto;

import java.util.List;

public interface SickDayRemainAggregator {
    long aggregate(@NonNull List<SickDayTaskDto> sickDays);

    FreeSickDayExtraditionType getType();
}
