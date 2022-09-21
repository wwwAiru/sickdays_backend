package ru.egartech.sickday.util;

import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.status.StatusDto;
import ru.egartech.sickday.domain.position.PositionType;
import ru.egartech.sickday.domain.status.SickDayStatusType;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.model.EmployeeTaskDto;
import ru.egartech.sickday.model.SickDayAssignerDto;
import ru.egartech.sickday.model.SickDayTaskDto;

import java.util.*;

public class Generator {
    public static List<SickDayTaskDto> generateSickDays(int count) {
        List<SickDayTaskDto> sickDayTaskDtos = new ArrayList<>();
        if (count < 1) {
            return sickDayTaskDtos;
        }

        for (int i = 1; i <= count; i++) {
            EmployeeTaskDto employeeTaskDto = EmployeeTaskDto.builder()
                    .id(UUID.randomUUID().toString())
                    .position(PositionType.values()[new Random().nextInt(PositionType.values().length)].getName())
                    .build();

            List<SickDayAssignerDto> assignerDtos = List.of(SickDayAssignerDto.builder()
                    .id(UUID.randomUUID().toString())
                    .build());

            SickDayTaskDto sickDayTaskDto = SickDayTaskDto.builder()
                    .id(String.valueOf(i))
                    .name(String.valueOf(i))
                    .status(SickDayStatusType.NEW.getAsString())
                    .type(SickDayType.SICK_DAY.name())
                    .employee(employeeTaskDto)
                    .assigners(assignerDtos)
                    .startDate(String.valueOf(new Date().getTime()))
                    .endDate(String.valueOf(new Date().getTime()))
                    .build();

            sickDayTaskDtos.add(sickDayTaskDto);
        }

        return sickDayTaskDtos;
    }

    public static List<TaskDto> generateTasks(int count) {
        List<TaskDto> taskDtos = new ArrayList<>();
        if (count < 1) {
            return taskDtos;
        }

        for (int i = 1; i <= count; i++) {
            TaskDto taskDto = TaskDto.builder()
                    .id(String.valueOf(i))
                    .name(String.valueOf(i))
                    .assigners(List.of())
                    .status(new StatusDto())
                    .customFields(new HashMap<>())
                    .build();

            taskDtos.add(taskDto);
        }

        return taskDtos;
    }
}
