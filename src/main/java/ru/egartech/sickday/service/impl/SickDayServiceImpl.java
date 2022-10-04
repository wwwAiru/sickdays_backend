package ru.egartech.sickday.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.assigner.AssignerDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipValueDto;
import ru.egartech.sdk.dto.task.serialization.CreateTaskDto;
import ru.egartech.sdk.dto.task.serialization.UpdateTaskDto;
import ru.egartech.sdk.dto.task.serialization.assigner.Assigner;
import ru.egartech.sdk.dto.task.serialization.customfield.request.CustomFieldRequest;
import ru.egartech.sdk.dto.task.serialization.customfield.update.BindFieldDto;
import ru.egartech.sickday.domain.branch.FreeSickDaysByBranchResolver;
import ru.egartech.sickday.domain.position.PositionType;
import ru.egartech.sickday.domain.position.SickDayListIdByPositionResolver;
import ru.egartech.sickday.domain.remain.FreeSickDayExtraditionType;
import ru.egartech.sickday.domain.remain.SickDayRemainResolver;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.exception.SickDayApplicationException;
import ru.egartech.sickday.exception.employee.EmployeePositionNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayCreateException;
import ru.egartech.sickday.exception.sickday.SickDayException;
import ru.egartech.sickday.exception.sickday.SickDayNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayUpdateException;
import ru.egartech.sickday.manager.TaskManager;
import ru.egartech.sickday.mapper.TaskMapper;
import ru.egartech.sickday.model.SickDayAssignerDto;
import ru.egartech.sickday.model.SickDayRemainDto;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FieldIdsProperties;
import ru.egartech.sickday.service.SickDayService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.isNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class SickDayServiceImpl implements SickDayService {
    private final TaskManager taskManager;
    private final SickDayRemainResolver sickDayRemainResolver;
    private final SickDayListIdByPositionResolver sickDayListIdByPositionResolver;
    private final FreeSickDaysByBranchResolver freeSickDaysByBranchResolver;
    private final FieldIdsProperties fieldIdsProperties;
    private final TaskMapper taskMapper;

    @Override
    public List<SickDayTaskDto> getSickDayByIds(List<String> ids) {
        return taskMapper.toListDto(taskManager.findByIds(ids));
    }

    @Override
    public SickDayTaskDto addSickDay(SickDayTaskDto sickDayTaskDto) throws SickDayException {
        PositionType positionType = Optional.ofNullable(PositionType.valueOfPosition(sickDayTaskDto
                .getEmployee()
                .getPosition()
                .toLowerCase()
        )).orElseThrow(EmployeePositionNotFoundException::new);

        Integer sickDaysListByPosition = sickDayListIdByPositionResolver.getSickDaysListId(positionType);
        TaskDto newTaskDto;
        try {
            CreateTaskDto createTaskDto = CreateTaskDto.builder()
                                                       .name(sickDayTaskDto.getName())
                                                       .status(sickDayTaskDto.getStatus())
                                                       .build();

            List<AssignerDto> assigners = getAssigners(sickDayTaskDto.getAssigners());
            newTaskDto = taskManager.create(sickDaysListByPosition, createTaskDto);
            sickDayTaskDto.setId(newTaskDto.getId());
            newTaskDto.setAssigners(assigners);
            log.info("Creating task with id:{}", newTaskDto.getId());
        } catch (RuntimeException e) {
            SickDayCreateException sickDayCreateException = new SickDayCreateException(e);
            // id clickup'а создается только тогда, когда есть спец. план
            // чтобы не путаться, здесь указана пустая строка
            sickDayCreateException.setId("");
            throw sickDayCreateException;
        }

        try {
            // связывание id нового больничного с создавшим его сотрудником
            String[] assignerIds = getAssignersIds(sickDayTaskDto.getAssigners());
            UpdateTaskDto updateTaskDto = UpdateTaskDto.builder()
                                                       .id(newTaskDto.getId())
                                                       .assignees(Assigner.link(assignerIds))
                                                       .status(sickDayTaskDto.getStatus())
                                                       .customFields(getBindFields(sickDayTaskDto))
                                                       .build();

            TaskDto updatedTaskDto = taskManager.update(updateTaskDto);
            log.info("Updating task with id:{}", updatedTaskDto.getId());
        } catch (RuntimeException e) {
            SickDayUpdateException sickDayUpdateException = new SickDayUpdateException(e);
            sickDayUpdateException.setId(newTaskDto.getId());
            throw sickDayUpdateException;
        }
        return sickDayTaskDto;
    }

    @Override
    public SickDayTaskDto getSickDayById(String sickDayId) {
        return taskMapper.toDto(taskManager
                .findById(sickDayId)
                .orElseThrow(() -> {
                    SickDayNotFoundException ex = new SickDayNotFoundException();
                    ex.setId(sickDayId);
                    return ex;
                })
        );
    }

    @Override
    public SickDayTaskDto updateSickDayById(String sickDayId,
                                            SickDayTaskDto sickDayTaskDto) throws SickDayException {
        sickDayTaskDto.setId(sickDayId);
        String[] assignerIds = getAssignersIds(sickDayTaskDto.getAssigners());

        UpdateTaskDto updateTaskDto = UpdateTaskDto.builder()
                                                   .id(sickDayId)
                                                   .assignees(Assigner.link(assignerIds))
                                                   .status(sickDayTaskDto.getStatus())
                                                   .customFields(getBindFields(sickDayTaskDto))
                                                   .build();

        TaskDto updatedTaskDto = taskManager.update(updateTaskDto);
        log.info("Updating task with id:{}", updatedTaskDto.getId());
        return sickDayTaskDto;
    }

    @Override
    public SickDayRemainDto getRemainSickDaysByIds(List<String> ids, String branch) {
        List<TaskDto> taskDtos = taskManager.findByIds(ids);
        FreeSickDayExtraditionType sickDayType = freeSickDaysByBranchResolver.getFreeSickDayType(branch);
        long leftSickDaysCount = sickDayRemainResolver.compute(sickDayType, taskMapper.toListDto(taskDtos));

        return SickDayRemainDto.builder()
                               .branch(branch)
                               .sickDayRemain(BigDecimal.valueOf(leftSickDaysCount))
                               .build();
    }

    @Override
    public SickDayRemainDto getRemainSickDaysByEgarId(String egarId, Integer listId) throws SickDayException {
        TaskDto taskDto = getTaskByEgarIdWithListId(egarId, listId);
        List<RelationshipValueDto> sickDays = getSickDaysByTask(taskDto, false);
        List<SickDayTaskDto> SickDayTaskDtos = taskMapper.toListDto(sickDays, id -> taskManager
                .findById(id)
                .orElseThrow(SickDayApplicationException::new));

        String branch = taskDto
                .<DropdownFieldDto>customField(fieldIdsProperties.getBranchId())
                .getValue()
                .getName();

        FreeSickDayExtraditionType sickDayType = freeSickDaysByBranchResolver.getFreeSickDayType(branch);
        long leftSickDaysCount = sickDayRemainResolver.compute(sickDayType, SickDayTaskDtos);

        return SickDayRemainDto.builder()
                               .branch(branch)
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
                .orElseThrow(() -> {
                    SickDayNotFoundException sickDayNotFoundException = new SickDayNotFoundException();
                    sickDayNotFoundException.setId(sickDayId);
                    throw sickDayNotFoundException;
                });

        return taskMapper.toDto(sickDay, id -> taskManager
                .findById(sickDay.getId())
                .orElseThrow(SickDayApplicationException::new));
    }

    private List<AssignerDto> getAssigners(List<SickDayAssignerDto> sickDayAssignerDtos) {
        return sickDayAssignerDtos
                .stream()
                .map(e -> AssignerDto.builder()
                                     .id(e.getId())
                                     .build())
                .toList();
    }

    private String[] getAssignersIds(List<SickDayAssignerDto> sickDayAssignerDtos) {
        return sickDayAssignerDtos
                .stream()
                .map(SickDayAssignerDto::getId)
                .toList()
                .toArray(new String[0]);
    }

    private List<BindFieldDto> getBindFields(SickDayTaskDto sickDayTaskDto) {
        return List.of(
                BindFieldDto.of(fieldIdsProperties.getStartDateId(), sickDayTaskDto.getStartDate()),
                BindFieldDto.of(fieldIdsProperties.getEndDateId(), sickDayTaskDto.getEndDate()),
                BindFieldDto.of(
                        fieldIdsProperties.getSickDaysType(),
                        SickDayType.valueOf(sickDayTaskDto.getType()).getOrderindex()
                ),
                BindFieldDto.linkTask(fieldIdsProperties.getSickDaysId(), sickDayTaskDto.getEmployee().getId())
        );
    }

    private TaskDto getTaskByEgarIdWithListId(String egarId, Integer listId) {
        CustomFieldRequest<String> customFieldRequest = CustomFieldRequest.<String>builder()
                                                                          .fieldId(fieldIdsProperties.getEgarId())
                                                                          .value(egarId)
                                                                          .build();

        if (isNull(listId)) {
            return taskManager.findTasksByCustomFields(customFieldRequest)
                              .stream()
                              .findFirst()
                              .orElseThrow(SickDayApplicationException::new)
                              .getFirstTask();
        }
        return Optional.of(taskManager.findTasksByCustomFields(listId, customFieldRequest))
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
                ? sickDaysValues.orElseThrow(() -> {
            SickDayNotFoundException sickDayNotFoundException = new SickDayNotFoundException();
            sickDayNotFoundException.setId(taskDto.getId());
            throw sickDayNotFoundException;
        })
                : sickDaysValues.get();
    }
}
