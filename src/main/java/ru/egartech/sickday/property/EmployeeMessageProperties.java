package ru.egartech.sickday.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@ConfigurationProperties(prefix = "messages.exception.employees")
@Component
public class EmployeeMessageProperties {
    private String notFound;
    private String notFoundBranch;
}
