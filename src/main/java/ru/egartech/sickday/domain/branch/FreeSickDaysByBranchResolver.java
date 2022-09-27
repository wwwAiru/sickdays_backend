package ru.egartech.sickday.domain.branch;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.exception.employee.EmployeeBranchNotFoundException;
import ru.egartech.sickday.property.BranchesProperties;

@Component
@RequiredArgsConstructor
public class FreeSickDaysByBranchResolver {
    private final BranchesProperties branchesProperties;

    public FreeSickDayExtraditionType getFreeSickDayType(@NonNull String branch) {
        BranchType branchType = BranchType.valueOfBranch(branch.toLowerCase());
        if (branchesProperties.getPerYear().contains(branchType)) return FreeSickDayExtraditionType.YEAR;
        if (branchesProperties.getPerQuarter().contains(branchType)) return FreeSickDayExtraditionType.QUARTER;
        if (branchesProperties.getPerWork().contains(branchType)) return FreeSickDayExtraditionType.WORK;
        throw new EmployeeBranchNotFoundException();
    }
}
