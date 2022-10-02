package ru.egartech.sickday.domain.position;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.egartech.sickday.property.ListIdsProperties;

@Component
@RequiredArgsConstructor
public class SickDayListIdByPositionResolver {
    private final ListIdsProperties listIdsProperties;

    public Integer getSickDaysListId(PositionType positionType) {
        return switch (positionType) {
            case DEVELOPER -> listIdsProperties.getDevelopersSickDaysId();
            case TESTER -> listIdsProperties.getTestersSickDaysId();
            case ANALYTIC -> listIdsProperties.getAnalyticsSickDaysId();
        };
    }
}
