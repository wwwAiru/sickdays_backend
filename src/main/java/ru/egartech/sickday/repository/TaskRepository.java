package ru.egartech.sickday.repository;

import org.springframework.stereotype.Repository;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.TasksDto;
import ru.egartech.sdk.dto.task.serialization.customfield.request.CustomFieldRequest;
import ru.egartech.sdk.dto.task.serialization.customfield.update.BindFieldDto;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository {

    List<TaskDto> findByIds(List<String> ids);

    Optional<TaskDto> findById(String id);

    TasksDto findTasksByCustomFields(int listId, CustomFieldRequest<?>... customFieldRequest);

    List<TasksDto> findTasksByCustomFields(CustomFieldRequest<?>... customFieldRequest);

    // clickup создаёт указанное id для таски, только если есть спец. план
    TaskDto create(Integer listId, TaskDto taskDto);

    TaskDto update(TaskDto taskDto, BindFieldDto... bindFieldDtos);

}
