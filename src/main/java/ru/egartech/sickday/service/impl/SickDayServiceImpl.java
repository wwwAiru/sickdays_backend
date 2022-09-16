package ru.egartech.sickday.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.egartech.sdk.api.ListTaskClient;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownOption;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipValueDto;
import ru.egartech.sdk.dto.task.serialization.CreateTaskDto;
import ru.egartech.sdk.dto.task.serialization.RequestTaskDto;
import ru.egartech.sdk.dto.task.serialization.UpdateTaskDto;
import ru.egartech.sdk.dto.task.serialization.customfield.request.CustomFieldRequest;
import ru.egartech.sdk.dto.task.serialization.customfield.update.BindFieldDto;
import ru.egartech.sickday.domain.branch.FreeSickDaysByBranchResolver;
import ru.egartech.sickday.domain.position.PositionType;
import ru.egartech.sickday.domain.position.SickDayListIdByPositionResolver;
import ru.egartech.sickday.domain.remain.SickDayRemainResolver;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.exception.SickDayApplicationException;
import ru.egartech.sickday.exception.employee.EmployeePositionNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayException;
import ru.egartech.sickday.exception.sickday.SickDayNotFoundException;
import ru.egartech.sickday.mapper.SickDayMapper;
import ru.egartech.sickday.model.AssignerDto;
import ru.egartech.sickday.model.SickDayRemainDto;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FieldIdsProperties;
import ru.egartech.sickday.service.SickDayService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SickDayServiceImpl implements SickDayService {
    private final ListTaskClient listTaskClient;
    private final SickDayRemainResolver sickDayRemainResolver;
    private final SickDayListIdByPositionResolver sickDayListIdByPositionResolver;
    private final FieldIdsProperties fieldIdsProperties;
    private final SickDayMapper sickDayMapper;

    @Override
    public List<SickDayTaskDto> getSickDayByIds(List<String> ids) {
        return getSickDaysByTheirIds(ids);
    }

    @Override
    public SickDayTaskDto addSickDay(SickDayTaskDto sickDayTaskDto) throws SickDayException {
        // clickup создаёт указанное id для таски, только если есть спец. план
        RequestTaskDto createTaskDto = CreateTaskDto
                .ofName(sickDayTaskDto.getName())
                .setStatus(sickDayTaskDto.getStatus());

        Integer sickDaysListByPosition = sickDayListIdByPositionResolver.getSickDaysListId(
                Optional.ofNullable(PositionType.valueOfPosition(
                        sickDayTaskDto
                                .getEmployee()
                                .getPosition()
                                .toLowerCase()
                )).orElseThrow(EmployeePositionNotFoundException::new)
        );
        TaskDto newTaskDto = listTaskClient.createTask(sickDaysListByPosition, createTaskDto);
        sickDayTaskDto.setId(newTaskDto.getId());
        log.info("Creating task with id {}", newTaskDto.getId());

        // связывание id нового больничного с создавшим его сотрудником
        RequestTaskDto updateTaskDto = UpdateTaskDto
                .ofTaskId(newTaskDto.getId())
                .linkTask(fieldIdsProperties.getSickDaysId(), sickDayTaskDto.getEmployee().getId())
                .assignTo(sickDayTaskDto.getAssigners()
                        .stream()
                        .map(AssignerDto::getId)
                        .map(String::valueOf)
                        .toList()
                        .toArray(new String[]{}))
                .bindCustomFields(
                        BindFieldDto.of(fieldIdsProperties.getStartDateId(), sickDayTaskDto.getStartDate()),
                        BindFieldDto.of(fieldIdsProperties.getEndDateId(), sickDayTaskDto.getEndDate()),
                        BindFieldDto.of(
                                fieldIdsProperties.getSickDaysType(),
                                SickDayType.valueOf(sickDayTaskDto.getType()).getOrderindex()
                        )
                );
        listTaskClient.updateTask(updateTaskDto);
        log.info("Updating task with id: {}", updateTaskDto.getId());
        return sickDayTaskDto;
    }

    @Override
    public SickDayTaskDto getSickDayById(String sickDayId) {
        TaskDto sickDayTask = listTaskClient.getTaskById(sickDayId, false);
        return sickDayMapper.toDto(sickDayTask);
    }

    @Override
    public SickDayTaskDto updateSickDayById(String sickDayId,
                                            SickDayTaskDto sickDayTaskDto) throws SickDayException {
        sickDayTaskDto.setId(sickDayId);
        RequestTaskDto updateTaskDto = UpdateTaskDto.ofTaskId(sickDayId)
                .setName(sickDayTaskDto.getName())
                .assignTo(sickDayTaskDto.getAssigners()
                        .stream()
                        .map(AssignerDto::getId)
                        .map(String::valueOf)
                        .toList()
                        .toArray(new String[]{}))
                .setStatus(sickDayTaskDto.getStatus())
                .bindCustomFields(
                        BindFieldDto.of(fieldIdsProperties.getStartDateId(), sickDayTaskDto.getStartDate()),
                        BindFieldDto.of(fieldIdsProperties.getEndDateId(), sickDayTaskDto.getEndDate()),
                        BindFieldDto.of(
                                fieldIdsProperties.getSickDaysType(),
                                SickDayType.valueOf(sickDayTaskDto.getType()).getOrderindex()
                        )
                );
        listTaskClient.updateTask(updateTaskDto);
        log.info("Updating task with id: {}", updateTaskDto.getId());
        return sickDayTaskDto;
    }

    @Override
    public SickDayRemainDto getRemainSickDaysByIds(List<String> ids, String branch) {
        List<SickDayTaskDto> sickDayTaskDtos = getSickDaysByTheirIds(ids);
        long leftSickDaysCount = sickDayRemainResolver.compute(
                FreeSickDaysByBranchResolver.getFreeSickDayType(branch), sickDayTaskDtos);

        return SickDayRemainDto.builder()
                .branch(branch)
                .sickDayRemain(BigDecimal.valueOf(leftSickDaysCount))
                .build();
    }

    @Override
    public SickDayRemainDto getRemainSickDaysByEgarId(String egarId, Integer listId) throws SickDayException {
        TaskDto taskDto = getTaskByEgarIdWithListId(egarId, listId);
        List<RelationshipValueDto> sickDays = getSickDaysByTask(taskDto, false);
        List<SickDayTaskDto> SickDayTaskDtos = sickDayMapper.toListDto(
                sickDays,
                id -> listTaskClient.getTaskById(id, false)
        );

        DropdownOption branch = taskDto
                .<DropdownFieldDto>customField(fieldIdsProperties.getBranchId())
                .getValue();

        long leftSickDaysCount = sickDayRemainResolver
                .compute(FreeSickDaysByBranchResolver
                        .getFreeSickDayType(branch.getName()), SickDayTaskDtos);

        return SickDayRemainDto.builder()
                .branch(branch.getName())
                .sickDayRemain(BigDecimal.valueOf(leftSickDaysCount))
                .build();
    }

    @Override
    public SickDayTaskDto getSickDayByIdAndEgarId(String egarId,
                                                  String sickDayId,
                                                  Integer listId) throws SickDayException {
        TaskDto taskDto = getTaskByEgarIdWithListId(egarId, listId);
        List<RelationshipValueDto> sickDays = getSickDaysByTask(taskDto, true);
        RelationshipValueDto sickDay = sickDays
                .stream()
                .filter(s -> Objects.equals(s.getId(), sickDayId))
                .findFirst()
                .orElseThrow(SickDayNotFoundException::new);

        return sickDayMapper.toDto(
                sickDay,
                id -> listTaskClient.getTaskById(sickDay.getId(), false)
        );
    }

    private List<SickDayTaskDto> getSickDaysByTheirIds(List<String> ids) {
        List<SickDayTaskDto> sickDays = new ArrayList<>();
        ids.forEach(id -> {
            TaskDto sickDayTask = listTaskClient.getTaskById(id, false);
            sickDays.add(sickDayMapper.toDto(sickDayTask));
        });
        return sickDays;
    }

    private TaskDto getTaskByEgarIdWithListId(String egarId, Integer listId) {
        if (isNull(listId)) {
            return listTaskClient.getTasksByCustomFields(
                            CustomFieldRequest
                                    .create()
                                    .setFieldId(fieldIdsProperties.getEgarId())
                                    .setValue(egarId)
                    )
                    .stream()
                    .findFirst()
                    .orElseThrow(SickDayApplicationException::new)
                    .getFirstTask();
        }
        return Optional.of(listTaskClient.getTasksByCustomFields(listId, CustomFieldRequest
                        .create()
                        .setFieldId(fieldIdsProperties.getEgarId())
                        .setValue(egarId)
                ))
                .orElseThrow(SickDayApplicationException::new)
                .getFirstTask();
    }

    private List<RelationshipValueDto> getSickDaysByTask(TaskDto taskDto, Boolean isSickDaysNeeds) {
        Optional<RelationshipFieldDto> sickDays = Optional.ofNullable(
                taskDto.customField(fieldIdsProperties.getSickDaysId())
        );
        Optional<List<RelationshipValueDto>> sickDaysValues = sickDays.map(RelationshipFieldDto::getValue);
        return isSickDaysNeeds
                // если больничных нет, то отдается пустой список
                ? sickDaysValues.orElseThrow(SickDayNotFoundException::new)
                : sickDaysValues.get();
    }
}
