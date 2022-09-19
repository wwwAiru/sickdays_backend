package ru.egartech.sickday.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.assigner.AssignerDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.dropdown.DropdownOption;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipFieldDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipValueDto;
import ru.egartech.sdk.dto.task.serialization.customfield.request.CustomFieldRequest;
import ru.egartech.sdk.dto.task.serialization.customfield.update.BindFieldDto;
import ru.egartech.sickday.domain.branch.FreeSickDaysByBranchResolver;
import ru.egartech.sickday.domain.position.PositionType;
import ru.egartech.sickday.domain.position.SickDayListIdByPositionResolver;
import ru.egartech.sickday.domain.remain.SickDayRemainResolver;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.exception.SickDayApplicationException;
import ru.egartech.sickday.exception.employee.EmployeePositionNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayCreateException;
import ru.egartech.sickday.exception.sickday.SickDayException;
import ru.egartech.sickday.exception.sickday.SickDayNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayUpdateException;
import ru.egartech.sickday.mapper.SickDayMapper;
import ru.egartech.sickday.model.SickDayRemainDto;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FieldIdsProperties;
import ru.egartech.sickday.repository.TaskRepository;
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
    private final TaskRepository taskRepository;
    private final SickDayRemainResolver sickDayRemainResolver;
    private final SickDayListIdByPositionResolver sickDayListIdByPositionResolver;
    private final FieldIdsProperties fieldIdsProperties;
    private final SickDayMapper sickDayMapper;

    @Override
    public List<SickDayTaskDto> getSickDayByIds(List<String> ids) {
        return sickDayMapper.toListDto(taskRepository.findByIds(ids));
    }

    @Override
    public SickDayTaskDto addSickDay(SickDayTaskDto sickDayTaskDto) throws SickDayException {
        Integer sickDaysListByPosition = sickDayListIdByPositionResolver.getSickDaysListId(
                Optional.ofNullable(PositionType.valueOfPosition(
                        sickDayTaskDto
                                .getEmployee()
                                .getPosition()
                                .toLowerCase()
                )).orElseThrow(EmployeePositionNotFoundException::new)
        );

        TaskDto newTaskDto;
        try {
            newTaskDto = taskRepository.create(sickDaysListByPosition, sickDayMapper.toDto(sickDayTaskDto));
            sickDayTaskDto.setId(newTaskDto.getId());
            newTaskDto.setAssigners(sickDayTaskDto.getAssigners()
                    .stream()
                    .map(a -> AssignerDto.builder()
                            .id(a.getId())
                            .build())
                    .toList()
            );
            log.info("Creating task with id:{}", newTaskDto.getId());
        } catch (RuntimeException e) {
            throw new SickDayCreateException();
        }

        try {
            // связывание id нового больничного с создавшим его сотрудником
            TaskDto updateTaskDto = taskRepository.update(newTaskDto,
                    BindFieldDto.of(fieldIdsProperties.getStartDateId(), sickDayTaskDto.getStartDate()),
                    BindFieldDto.of(fieldIdsProperties.getEndDateId(), sickDayTaskDto.getEndDate()),
                    BindFieldDto.of(
                            fieldIdsProperties.getSickDaysType(),
                            SickDayType.valueOf(sickDayTaskDto.getType()).getOrderindex()
                    )
            );
            log.info("Updating task with id:{}", updateTaskDto.getId());
        } catch (RuntimeException e) {
            throw new SickDayUpdateException();
        }
        return sickDayTaskDto;
    }

    @Override
    public SickDayTaskDto getSickDayById(String sickDayId) {
        return sickDayMapper.toDto(taskRepository
                .findById(sickDayId)
                .orElseThrow(() -> {
                    SickDayNotFoundException ex = new SickDayNotFoundException();
                    ex.setId(sickDayId);
                    return ex;
                }));
    }

    @Override
    public SickDayTaskDto updateSickDayById(String sickDayId,
                                            SickDayTaskDto sickDayTaskDto) throws SickDayException {
        sickDayTaskDto.setId(sickDayId);
        TaskDto updateTaskDto = taskRepository.update(sickDayMapper.toDto(sickDayTaskDto),
                BindFieldDto.of(fieldIdsProperties.getSickDaysId(), sickDayTaskDto.getEmployee().getId()),
                BindFieldDto.of(fieldIdsProperties.getStartDateId(), sickDayTaskDto.getStartDate()),
                BindFieldDto.of(fieldIdsProperties.getEndDateId(), sickDayTaskDto.getEndDate()),
                BindFieldDto.of(
                        fieldIdsProperties.getSickDaysType(),
                        SickDayType.valueOf(sickDayTaskDto.getType()).getOrderindex()
                )
        );
        log.info("Updating task with id:{}", updateTaskDto.getId());
        return sickDayTaskDto;
    }

    @Override
    public SickDayRemainDto getRemainSickDaysByIds(List<String> ids, String branch) {
        List<TaskDto> taskDtos = taskRepository.findByIds(ids);
        long leftSickDaysCount = sickDayRemainResolver.compute(
                FreeSickDaysByBranchResolver.getFreeSickDayType(branch), sickDayMapper.toListDto(taskDtos));

        return SickDayRemainDto.builder()
                .branch(branch)
                .sickDayRemain(BigDecimal.valueOf(leftSickDaysCount))
                .build();
    }

    @Override
    public SickDayRemainDto getRemainSickDaysByEgarId(String egarId, Integer listId) throws SickDayException {
        TaskDto taskDto = getTaskByEgarIdWithListId(egarId, listId);
        List<RelationshipValueDto> sickDays = getSickDaysByTask(taskDto, false);
        List<SickDayTaskDto> SickDayTaskDtos = sickDayMapper.toListDto(sickDays, id -> taskRepository
                        .findById(id)
                        .orElseThrow(SickDayApplicationException::new));

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

        return sickDayMapper.toDto(sickDay, id -> taskRepository
                .findById(sickDay.getId())
                .orElseThrow(SickDayApplicationException::new));
    }

    private TaskDto getTaskByEgarIdWithListId(String egarId, Integer listId) {
        if (isNull(listId)) {
            return taskRepository.findTasksByCustomFields(
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
        return Optional.of(taskRepository.findTasksByCustomFields(listId, CustomFieldRequest
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
