package ru.egartech.sickday.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.egartech.sickday.domain.branch.BranchType;

import java.util.List;

@Data
@ConfigurationProperties(prefix = "clickup.branches")
@Component
public class BranchesProperties {
    private List<BranchType> perYear;
    private List<BranchType> perQuarter;
    private List<BranchType> perWork;
}
