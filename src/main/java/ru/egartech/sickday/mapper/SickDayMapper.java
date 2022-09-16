package ru.egartech.sickday.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownOption;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipValueDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.text.TextFieldDto;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.exception.employee.EmployeeNotFoundException;
import ru.egartech.sickday.exception.sickday.*;
import ru.egartech.sickday.model.AssignerDto;
import ru.egartech.sickday.model.EmployeeTaskDto;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FieldIdsProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SickDayMapper {
    private final FieldIdsProperties fieldIdsProperties;

    public SickDayTaskDto toDto(TaskDto sickDayTask) {
        return buildSickDayTaskDto(sickDayTask);
    }

    public SickDayTaskDto toDto(RelationshipValueDto sickDay,
                                Function<String, TaskDto> taskDtoFunction) {
        TaskDto sickDayTask = taskDtoFunction.apply(sickDay.getId());
        return buildSickDayTaskDto(sickDayTask);
    }

    public List<SickDayTaskDto> toListDto(List<RelationshipValueDto> sickDays,
                                          Function<String, TaskDto> taskDtoFunction) {
        List<SickDayTaskDto> SickDayTaskDtos = new ArrayList<>();
        sickDays.forEach(sickDay -> SickDayTaskDtos.add(toDto(sickDay, taskDtoFunction)));
        return SickDayTaskDtos;
    }

    private SickDayTaskDto buildSickDayTaskDto(TaskDto sickDayTask) {
        RelationshipFieldDto employeeRelationship = getEmployeeRelationshipFromSickDayTask(sickDayTask);

        return SickDayTaskDto.builder()
                .id(sickDayTask.getId())
                .name(sickDayTask.getName())
                .employee(EmployeeTaskDto.builder()
                        .id(getEmployeeValueFromRelationship(employeeRelationship).getId())
                        .name(getEmployeeValueFromRelationship(employeeRelationship).getName())
                        .build())
                .status(getSickDayStatus(sickDayTask))
                .type(getSickDayType(sickDayTask))
                .startDate(getSickDayDate(sickDayTask, fieldIdsProperties.getStartDateId()))
                .endDate(getSickDayDate(sickDayTask, fieldIdsProperties.getEndDateId()))
                .assigners(getAssigners(sickDayTask.getAssigners()))
                .build();
    }

    private List<AssignerDto> getAssigners(List<ru.egartech.sdk.dto.task.deserialization.customfield.assigner.AssignerDto> assignerDtos) {
        List<AssignerDto> assigners = new ArrayList<>();
        assignerDtos.forEach(assignerDto -> assigners.add(AssignerDto.builder()
                .id(assignerDto.getId())
                .build()
        ));

        return assigners;
    }

    private String getSickDayType(TaskDto sickDayTask) {
        DropdownOption sickDayTypeDropdownOption = Optional.ofNullable(sickDayTask.
                <DropdownFieldDto>customField(fieldIdsProperties.getSickDaysType())
                .getValue()
        ).orElseThrow(SickDayTypeNotFoundException::new);

        return SickDayType.valueOfLabel(sickDayTypeDropdownOption.getName().toLowerCase()).name();
    }

    private String getSickDayStatus(TaskDto sickDayTask) {
        return sickDayTask
                .getStatus()
                .getStatus();
    }

    private String getSickDayDate(TaskDto sickDayTask, String dateFieldId) {
        return Optional.ofNullable(
                sickDayTask
                        .<TextFieldDto>customField(dateFieldId)
                        .getValue()
        ).orElseThrow(SickDayDateNotFoundException::new);
    }

    private RelationshipFieldDto getEmployeeRelationshipFromSickDayTask(TaskDto sickDayTask) {
        return Optional.ofNullable(sickDayTask
                .<RelationshipFieldDto>customField(fieldIdsProperties.getSickDaysId())
        ).orElseThrow(EmployeeNotFoundException::new);
    }

    private RelationshipValueDto getEmployeeValueFromRelationship(RelationshipFieldDto relationshipFieldDto) {
        List<RelationshipValueDto> value = relationshipFieldDto.getValue();
        if (value.size() != 1) {
            throw new SickDayException();
        }
        return value.get(0);
    }
}
