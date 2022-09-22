package ru.egartech.sickday.domain.branch;

import lombok.NonNull;
import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.exception.employee.EmployeeBranchNotFoundException;

import java.util.List;

public class FreeSickDaysByBranchResolver {
    private static final List<String> PER_YEAR_BRANCHES = List.of(
            BranchType.MOSCOW.getAsString(),
            BranchType.SAMARA.getAsString(),
            BranchType.GOMEL.getAsString()
    );
    private static final List<String> PER_QUARTER_BRANCHES = List.of(
            BranchType.BRYANSK.getAsString(),
            BranchType.VLADIMIR.getAsString(),
            BranchType.TOMSK.getAsString()
    );
    private static final List<String> PER_WORK_BRANCHES = List.of(
            BranchType.PENZA.getAsString()
    );

    public static FreeSickDayExtraditionType getFreeSickDayType(@NonNull String branch) {
        if (PER_YEAR_BRANCHES.contains(branch.trim().toLowerCase())) return FreeSickDayExtraditionType.YEAR;
        if (PER_QUARTER_BRANCHES.contains(branch.trim().toLowerCase())) return FreeSickDayExtraditionType.QUARTER;
        if (PER_WORK_BRANCHES.contains(branch.trim().toLowerCase())) return FreeSickDayExtraditionType.WORK;
        throw new EmployeeBranchNotFoundException();
    }
}
