package ru.egartech.sickday.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "clickup.free-sick-days-count")
@Component
public class FreeSickDayCountProperties {
    private Integer perYear;
    private Integer perQuarter;
    private Integer perWork;
}
