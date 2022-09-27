package ru.egartech.sickday.domain.remain.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.egartech.sickday.property.FreeSickDayCountProperties;

@Component
@RequiredArgsConstructor
public abstract class AbstractRemainFreeSickDaysAggregator implements SickDayRemainAggregator{
    protected final FreeSickDayCountProperties freeSickDayCountProperties;
}
