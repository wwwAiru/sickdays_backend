package ru.egartech.sickday.repository;

import lombok.NonNull;
import org.springframework.stereotype.Repository;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.TasksDto;
import ru.egartech.sdk.dto.task.serialization.CreateTaskDto;
import ru.egartech.sdk.dto.task.serialization.UpdateTaskDto;
import ru.egartech.sdk.dto.task.serialization.customfield.request.CustomFieldRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository {

    List<TaskDto> findByIds(@NonNull List<String> ids);

    Optional<TaskDto> findById(@NonNull String id);

    TasksDto findTasksByCustomFields(int listId, CustomFieldRequest<?>... customFieldRequest);

    List<TasksDto> findTasksByCustomFields(CustomFieldRequest<?>... customFieldRequest);

    // clickup создаёт указанное id у таски, только если есть спец. план
    TaskDto create(int listId, @NonNull CreateTaskDto createTaskDto);

    TaskDto update(@NonNull UpdateTaskDto updateTaskDto);

}
