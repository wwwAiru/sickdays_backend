package ru.egartech.sickday.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.egartech.sickday.domain.position.PositionType;

@Component
@Data
@ConfigurationProperties(prefix = "clickup.list")
public class ListIdsProperties {
    private int developersSickDaysId;
    private int testersSickDaysId;
    private int analyticsSickDaysId;

    public Integer sickDaysListIdByPosition(PositionType positionType) {
        return switch (positionType) {
            case DEVELOPER -> developersSickDaysId;
            case TESTER -> testersSickDaysId;
            case ANALYTIC -> analyticsSickDaysId;
        };
    }
}
