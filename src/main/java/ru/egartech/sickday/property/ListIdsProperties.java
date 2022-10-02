package ru.egartech.sickday.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "clickup.list")
public class ListIdsProperties {

    private int developersSickDaysId;
    private int testersSickDaysId;
    private int analyticsSickDaysId;

}
