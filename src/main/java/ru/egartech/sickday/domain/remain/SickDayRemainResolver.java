package ru.egartech.sickday.domain.remain;

import org.springframework.stereotype.Component;
import ru.egartech.sickday.domain.remain.aggregator.SickDayRemainAggregator;
import ru.egartech.sickday.model.SickDayTaskDto;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class SickDayRemainResolver {
    private final Map<FreeSickDayExtraditionType, SickDayRemainAggregator> sickDayLeftAggregators;

    public SickDayRemainResolver(List<SickDayRemainAggregator> sickDayRemainAggregators) {
        this.sickDayLeftAggregators = sickDayRemainAggregators
                .stream()
                .collect(Collectors.toMap(SickDayRemainAggregator::getType, Function.identity()));
    }

    public long compute(FreeSickDayExtraditionType freeSickDayExtraditionType, List<SickDayTaskDto> sickDays) {
        return sickDayLeftAggregators.get(freeSickDayExtraditionType).aggregate(sickDays);
    }
}
