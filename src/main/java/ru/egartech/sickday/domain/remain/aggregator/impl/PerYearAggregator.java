package ru.egartech.sickday.domain.remain.aggregator.impl;

import org.springframework.stereotype.Component;
import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.domain.remain.aggregator.AbstractRemainFreeSickDaysAggregator;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FreeSickDayCountProperties;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class PerYearAggregator extends AbstractRemainFreeSickDaysAggregator {
    public PerYearAggregator(FreeSickDayCountProperties freeSickDayCountProperties) {
        super(freeSickDayCountProperties);
    }

    @Override
    public Long aggregate(List<SickDayTaskDto> sickDays) {
        long usedSickDaysCount = sickDays
                .stream()
                .filter(sickDay -> sickDay.getType().equals(SickDayType.SICK_DAY.name()))
                .filter(this::isSickDayOnThisYear)
                .count();

        return Math.max(0, freeSickDayCountProperties.getPerYear() - usedSickDaysCount);
    }

    @Override
    public FreeSickDayExtraditionType getType() {
        return FreeSickDayExtraditionType.YEAR;
    }

    private boolean isSickDayOnThisYear(SickDayTaskDto sickDay) {
        Date startDate = new Date(Long.parseLong(sickDay.getStartDate()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Integer sickDayStartDateYear = calendar.get(Calendar.YEAR);
        Integer currentYear = LocalDate.now().getYear();
        return currentYear.equals(sickDayStartDateYear);
    }
}
