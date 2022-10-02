package ru.egartech.sickday.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "clickup.field")
public class FieldIdsProperties {

    private String egarId;
    private String sickDaysId;
    private String sickDaysType;
    private String branchId;
    private String startDateId;
    private String endDateId;

}
