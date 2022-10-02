package ru.egartech.sickday.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "messages.exception.sick-days")
@Component
public class SickDayMessageProperties {
    private String create;
    private String update;
    private String notFoundId;
    private String notFoundDate;
    private String notFoundStatus;
    private String notFoundType;
}
