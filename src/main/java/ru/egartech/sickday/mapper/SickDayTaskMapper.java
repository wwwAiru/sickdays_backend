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
import ru.egartech.sdk.dto.task.deserialization.status.StatusDto;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FieldIdsProperties;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SickDayTaskMapper implements DtoMapper<SickDayTaskDto, TaskDto> {
    private final FieldIdsProperties fieldIdsProperties;

    @Override
    public TaskDto toDto(SickDayTaskDto sickDayTaskDto) {
        return buildTaskDto(sickDayTaskDto);
    }

    @Override
    public List<TaskDto> toListDto(List<SickDayTaskDto> sickDayTaskDtos) {
        return sickDayTaskDtos
                .stream()
                .map(this::toDto)
                .toList();
    }

    private TaskDto buildTaskDto(SickDayTaskDto sickDayTaskDto) {
        List<AssignerDto> assignerDtos = sickDayTaskDto.getAssigners()
                .stream()
                .map(e -> AssignerDto.builder().id(sickDayTaskDto.getId()).build())
                .toList();

        StatusDto statusDto = StatusDto.builder()
                .status(sickDayTaskDto.getStatus())
                .build();

        return TaskDto.builder()
                .id(sickDayTaskDto.getId())
                .name(sickDayTaskDto.getName())
                .assigners(assignerDtos)
                .status(statusDto)
                .customFields(getSickDayTaskDtoShortCustomFields(sickDayTaskDto))
                .build();
    }

    private Map<String, CustomField<?>> getSickDayTaskDtoShortCustomFields(SickDayTaskDto sickDayTaskDto) {
        Map<String, CustomField<?>> map = new HashMap<>();

        map.put(fieldIdsProperties.getStartDateId(), getStartDate(sickDayTaskDto));
        map.put(fieldIdsProperties.getEndDateId(), getEndDate(sickDayTaskDto));
        map.put(fieldIdsProperties.getSickDaysType(), getSickDayType(sickDayTaskDto));
        map.put(fieldIdsProperties.getSickDaysId(), getSickDays(List.of(sickDayTaskDto)));

        return map;
    }

    private TextFieldDto getStartDate(SickDayTaskDto sickDayTaskDto) {
        return TextFieldDto.builder()
                .id(fieldIdsProperties.getStartDateId())
                .value(sickDayTaskDto.getStartDate())
                .build();
    }

    private TextFieldDto getEndDate(SickDayTaskDto sickDayTaskDto) {
        return TextFieldDto.builder()
                .id(fieldIdsProperties.getEndDateId())
                .value(sickDayTaskDto.getEndDate())
                .build();
    }

    private DropdownFieldDto getSickDayType(SickDayTaskDto sickDayTaskDto) {
        DropdownTypeConfig dropdownTypeConfig = DropdownTypeConfig.builder()
                .labelOptions(
                        Arrays.stream(SickDayType.values())
                                .map(e -> DropdownOption.builder()
                                        .name(e.getAsString())
                                        .orderIndex(e.getOrderindex())
                                        .build()
                                )
                                .toList()
                ).build();

        DropdownOption dropdownOption = DropdownOption.builder()
                .name(SickDayType.valueOf(sickDayTaskDto.getType()).getAsString())
                .orderIndex(SickDayType.valueOf(sickDayTaskDto.getType()).getOrderindex())
                .build();

        return DropdownFieldDto.builder()
                .dropdownTypeConfig(dropdownTypeConfig)
                .id(fieldIdsProperties.getSickDaysType())
                .name(SickDayType.valueOf(sickDayTaskDto.getType()).getAsString())
                .value(dropdownOption)
                .build();
    }

    private RelationshipFieldDto getSickDays(List<SickDayTaskDto> sickDayTaskDtos) {
        List<RelationshipValueDto> relationshipValueDtos = sickDayTaskDtos
                .stream()
                .map(e -> RelationshipValueDto.builder()
                        .id(e.getId())
                        .build())
                .toList();

        return RelationshipFieldDto.builder()
                .id(fieldIdsProperties.getSickDaysId())
                .value(relationshipValueDtos)
                .build();
    }
}
