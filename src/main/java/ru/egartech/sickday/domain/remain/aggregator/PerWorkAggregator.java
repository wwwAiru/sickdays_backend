package ru.egartech.sickday.domain.remain.aggregator;

import org.springframework.stereotype.Component;
import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.model.SickDayTaskDto;

import java.util.List;

@Component
public class PerWorkAggregator implements SickDayRemainAggregator {
    @Override
    public long aggregate(List<SickDayTaskDto> sickDays) {
        PerYearAggregator perYearAggregator = new PerYearAggregator();
        return perYearAggregator.aggregate(sickDays);
    }

    @Override
    public FreeSickDayExtraditionType getType() {
        return FreeSickDayExtraditionType.WORK;
    }
}
