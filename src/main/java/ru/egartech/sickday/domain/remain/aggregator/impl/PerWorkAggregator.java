package ru.egartech.sickday.domain.remain.aggregator.impl;

import org.springframework.stereotype.Component;
import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.domain.remain.aggregator.AbstractRemainFreeSickDaysAggregator;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FreeSickDayCountProperties;

import java.util.List;

@Component
public class PerWorkAggregator extends AbstractRemainFreeSickDaysAggregator {
    public PerWorkAggregator(FreeSickDayCountProperties freeSickDayCountProperties) {
        super(freeSickDayCountProperties);
    }

    @Override
    public Long aggregate(List<SickDayTaskDto> sickDays) {
        PerYearAggregator perYearAggregator = new PerYearAggregator(freeSickDayCountProperties);
        return perYearAggregator.aggregate(sickDays);
    }

    @Override
    public FreeSickDayExtraditionType getType() {
        return FreeSickDayExtraditionType.WORK;
    }
}
