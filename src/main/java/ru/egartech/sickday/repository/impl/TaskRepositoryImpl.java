package ru.egartech.sickday.repository.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import ru.egartech.sdk.api.ListTaskClient;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.TasksDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.assigner.AssignerDto;
import ru.egartech.sdk.dto.task.serialization.CreateTaskDto;
import ru.egartech.sdk.dto.task.serialization.UpdateTaskDto;
import ru.egartech.sdk.dto.task.serialization.customfield.request.CustomFieldRequest;
import ru.egartech.sdk.dto.task.serialization.customfield.update.BindFieldDto;
import ru.egartech.sickday.repository.TaskRepository;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TaskRepositoryImpl implements TaskRepository {
    private final ListTaskClient listTaskClientImpl;

    @Override
    public List<TaskDto> findByIds(List<String> ids) {
        return ids
                .stream()
                .map(id -> listTaskClientImpl.getTaskById(id, false))
                .toList();
    }

    @Override
    public Optional<TaskDto> findById(String id) {
        try {
            return Optional.of(listTaskClientImpl.getTaskById(id, false));
        } catch (HttpClientErrorException e) {
            return Optional.empty();
        }
    }

    @Override
    public TasksDto findTasksByCustomFields(int listId, CustomFieldRequest<?>... customFieldRequest) {
        return listTaskClientImpl.getTasksByCustomFields(listId, customFieldRequest);
    }

    @Override
    public List<TasksDto> findTasksByCustomFields(CustomFieldRequest<?>... customFieldRequest) {
        return listTaskClientImpl.getTasksByCustomFields(customFieldRequest);
    }

    @Override
    public TaskDto create(Integer listId, TaskDto taskDto) {
        return listTaskClientImpl.createTask(listId, CreateTaskDto
                .ofName(taskDto.getName())
                .setStatus(taskDto.getStatus().getStatus()));
    }

    @Override
    public TaskDto update(TaskDto taskDto, BindFieldDto... bindFieldDtos) {
        return listTaskClientImpl.updateTask(UpdateTaskDto
                .ofTaskId(taskDto.getId())
                .assignTo(taskDto.getAssigners()
                        .stream()
                        .map(AssignerDto::getId)
                        .map(String::valueOf)
                        .toList()
                        .toArray(new String[]{}))
                .bindCustomFields(bindFieldDtos));
    }
}
