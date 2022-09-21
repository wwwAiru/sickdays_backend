package ru.egartech.sickday.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.assigner.AssignerDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.CustomField;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownOption;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownTypeConfig;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipValueDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.text.TextFieldDto;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.exception.employee.EmployeeNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayDateNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayTypeNotFoundException;
import ru.egartech.sickday.model.EmployeeTaskDto;
import ru.egartech.sickday.model.SickDayAssignerDto;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FieldIdsProperties;

import java.util.*;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class SickDayMapper {
    private final FieldIdsProperties fieldIdsProperties;

    public SickDayTaskDto toDto(TaskDto sickDayTask) {
        return buildSickDayTaskDto(sickDayTask);
    }

    public SickDayTaskDto toDto(RelationshipValueDto sickDay, Function<String, TaskDto> taskDtoFunction) {
        TaskDto sickDayTask = taskDtoFunction.apply(sickDay.getId());
        return buildSickDayTaskDto(sickDayTask);
    }

    public List<SickDayTaskDto> toListDto(List<TaskDto> taskDtos) {
        return taskDtos
                .stream()
                .map(this::toDto)
                .toList();
    }

    public List<SickDayTaskDto> toListDto(List<RelationshipValueDto> sickDays,
                                          Function<String, TaskDto> taskDtoFunction) {
        List<SickDayTaskDto> taskDtos = new ArrayList<>();
        sickDays.forEach(sickDay -> taskDtos.add(toDto(sickDay, taskDtoFunction)));
        return taskDtos;
    }

    public Map<String, CustomField<?>> getSickDayTaskDtoShortCustomFields(SickDayTaskDto sickDayTaskDto) {
        Map<String, CustomField<?>> map = new HashMap<>();

        map.put(fieldIdsProperties.getStartDateId(), TextFieldDto.builder()
                .id(fieldIdsProperties.getStartDateId())
                .value(sickDayTaskDto.getStartDate())
                .build());

        map.put(fieldIdsProperties.getEndDateId(), TextFieldDto.builder()
                .id(fieldIdsProperties.getEndDateId())
                .value(sickDayTaskDto.getEndDate())
                .build());

        map.put(fieldIdsProperties.getSickDaysType(), DropdownFieldDto.builder()
                .dropdownTypeConfig(DropdownTypeConfig.builder().labelOptions(
                        Arrays.stream(SickDayType.values())
                                .map(e -> new DropdownOption(
                                        "",
                                        e.getAsString(),
                                        e.getOrderindex(),
                                        0
                                ))
                                .toList()
                ).build())
                .id(fieldIdsProperties.getSickDaysType())
                .name(SickDayType.valueOf(sickDayTaskDto.getType()).getAsString())
                .value(new DropdownOption(
                        "",
                        SickDayType.valueOf(sickDayTaskDto.getType()).getAsString(),
                        SickDayType.valueOf(sickDayTaskDto.getType()).getOrderindex(),
                        0
                ))
                .build());

        map.put(fieldIdsProperties.getSickDaysId(), RelationshipFieldDto.builder()
                .id(fieldIdsProperties.getSickDaysId())
                .value(List.of(RelationshipValueDto.builder()
                        .id(sickDayTaskDto.getId())
                        .build()))
                .build());

        return map;
    }

    private SickDayTaskDto buildSickDayTaskDto(TaskDto sickDayTask) {
        RelationshipFieldDto employeeRelationship = getEmployeeRelationshipFromSickDayTask(sickDayTask);

        EmployeeTaskDto employeeTaskDto = EmployeeTaskDto.builder()
                .id(getEmployeeValueFromRelationship(employeeRelationship).getId())
                .build();

        return SickDayTaskDto.builder()
                .id(sickDayTask.getId())
                .name(sickDayTask.getName())
                .employee(employeeTaskDto)
                .status(getSickDayStatus(sickDayTask))
                .type(getSickDayType(sickDayTask))
                .startDate(getSickDayDate(sickDayTask, fieldIdsProperties.getStartDateId()))
                .endDate(getSickDayDate(sickDayTask, fieldIdsProperties.getEndDateId()))
                .assigners(getAssigners(sickDayTask.getAssigners()))
                .build();
    }

    public List<AssignerDto> mapAssigners(List<SickDayAssignerDto> assignerDtos) {
        return assignerDtos
                .stream()
                .map(a -> AssignerDto.builder().id(a.getId()).build())
                .toList();
    }

    public List<String> mapAssignersIds(List<AssignerDto> assignerDtos) {
        return assignerDtos
                .stream()
                .map(AssignerDto::getId)
                .map(String::valueOf)
                .toList();
    }

    private List<SickDayAssignerDto> getAssigners(List<AssignerDto> assignerDtos) {
        List<SickDayAssignerDto> assigners = new ArrayList<>();

        assignerDtos.forEach(assignerDto -> assigners.add(
                SickDayAssignerDto.builder()
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
            throw new EmployeeNotFoundException();
        }
        return value.get(0);
    }
}
