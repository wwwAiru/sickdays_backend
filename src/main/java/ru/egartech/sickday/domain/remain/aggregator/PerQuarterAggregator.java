package ru.egartech.sickday.domain.remain.aggregator;

import org.springframework.stereotype.Component;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.domain.remain.FreeSickDayCountType;
import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.model.SickDayTaskDto;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class PerQuarterAggregator implements SickDayRemainAggregator {
    @Override
    public Long aggregate(List<SickDayTaskDto> sickDays) {
        long usedSickDaysCount = sickDays
                .stream()
                .filter(sickDay -> sickDay.getType().equals(SickDayType.SICK_DAY.name()))
                .filter(this::isSickDayOnThisQuarter)
                .count();

        return Math.max(0, FreeSickDayCountType.PER_QUARTER.getCount() - usedSickDaysCount);
    }

    @Override
    public FreeSickDayExtraditionType getType() {
        return FreeSickDayExtraditionType.QUARTER;
    }

    private boolean isSickDayOnThisQuarter(SickDayTaskDto sickDay) {
        Date startDate = new Date(Long.parseLong(sickDay.getStartDate()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);

        Integer sickDayStartDateQuarter = getQuarter(calendar);
        calendar.setTime(new Date());
        Integer currentQuarter = getQuarter(calendar);
        return currentQuarter.equals(sickDayStartDateQuarter);
    }

    private int getQuarter(Calendar calendar) {
        int month = calendar.get(Calendar.MONTH);
        return
                month <= Calendar.MARCH
                        ? 1
                        : month <= Calendar.JUNE
                        ? 2
                        : month <= Calendar.SEPTEMBER
                        ? 3
                        : 4;
    }
}
