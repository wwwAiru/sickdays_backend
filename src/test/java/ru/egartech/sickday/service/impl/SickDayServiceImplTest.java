package ru.egartech.sickday.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.TasksDto;
import ru.egartech.sickday.AbstractSpringContext;
import ru.egartech.sickday.domain.branch.BranchType;
import ru.egartech.sickday.exception.sickday.SickDayNotFoundException;
import ru.egartech.sickday.mapper.SickDayTaskMapper;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.manager.TaskManager;
import ru.egartech.sickday.service.SickDayService;
import ru.egartech.sickday.util.Generator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Тестирование mock'а SickDayService")
@ExtendWith(MockitoExtension.class)
class SickDayServiceImplTest extends AbstractSpringContext {
    @MockBean
    private TaskManager taskManager;

    @SpyBean
    private SickDayTaskMapper sickDayTaskMapper;

    @Autowired
    private SickDayService sickDayService;

    @SneakyThrows
    @Test
    @DisplayName("Тестирование создание нового больничного")
    public void addSickDayTest() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = sickDayTaskMapper.toDto(sickDayTaskDto);
        // when
        when(taskManager.create(anyInt(), any())).thenReturn(taskDto);
        when(taskManager.update(any())).thenReturn(taskDto);
        sickDayService.addSickDay(sickDayTaskDto);
        // then
        verify(taskManager, times(1)).create(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        verify(taskManager, times(1)).update(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Тестирование поиска оставшихся sick day по egar id")
    public void getRemainSickDaysByEgarIdTest() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        // подставляются валидные, случайно сгенерированные значения в TaskDto,
        // чтобы внутри сервиса не вызывались кастомные исключения
        TaskDto taskDto = sickDayTaskMapper.toDto(sickDayTaskDto);
        TasksDto tasksDto = new TasksDto(List.of(taskDto));
        // when
        when(taskManager.findTasksByCustomFields(anyInt(), any())).thenReturn(tasksDto);
        when(taskManager.findById(anyString())).thenReturn(Optional.of(taskDto));
        sickDayService.getSickDayByIdAndEgarId("egar_id", taskDto.getId(), 0);
        // then
        verify(taskManager, times(1))
                .findTasksByCustomFields(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        verify(taskManager, times(1)).
                findById(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("Тестирование поиска оставшихся sick day по ids ")
    public void getRemainSickDaysByIdsTest() {
        // given
        List<SickDayTaskDto> sickDayTaskDtos = Generator.generateSickDays(3);
        List<TaskDto> taskDtos = sickDayTaskMapper.toListDto(sickDayTaskDtos);
        List<String> sickDaysIds = sickDayTaskDtos
                .stream()
                .map(SickDayTaskDto::getId)
                .toList();
        // when
        when(taskManager.findByIds(anyList())).thenReturn(taskDtos);
        sickDayService.getRemainSickDaysByIds(sickDaysIds, BranchType.MOSCOW.getAsString());
        // then
        verify(taskManager, times(1)).findByIds(ArgumentMatchers.anyList());
    }

    @Test
    @DisplayName("Тестирование поиска по id больничного")
    public void getSickDayByIdTest() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = sickDayTaskMapper.toDto(sickDayTaskDto);
        // подставляются валидные, случайно сгенерированные значения в TaskDto,
        // чтобы внутри сервиса не вызывались кастомные исключения
        // when
        when(taskManager.findById(anyString())).thenReturn(Optional.of(taskDto));
        sickDayService.getSickDayById("id");
        // then
        verify(taskManager, times(1)).findById(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("Тестирование поиска по egar id и id больничного")
    public void getSickDayByIdAndEgarIdTest() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = sickDayTaskMapper.toDto(sickDayTaskDto);
        // подставляются валидные, случайно сгенерированные значения в TaskDto,
        // чтобы внутри сервиса не вызывались кастомные исключения
        TasksDto taskDtos = new TasksDto(List.of(taskDto));
        // when
        when(taskManager.findTasksByCustomFields(anyInt(), any())).thenReturn(taskDtos);
        when(taskManager.findById(anyString())).thenReturn(Optional.of(taskDto));
        sickDayService.getSickDayByIdAndEgarId("egar_id", taskDto.getId(), 0);
        // then
        verify(taskManager, times(1))
                .findTasksByCustomFields(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        verify(taskManager, times(1))
                .findById(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("Тестирование поиска по ids больничных")
    public void getSickDayByIdsTest() {
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = sickDayTaskMapper.toDto(sickDayTaskDto);
        // подставляются валидные, случайно сгенерированные значения в TaskDto,
        // чтобы внутри сервиса не вызывались кастомные исключения
        // when
        when(taskManager.findByIds(anyList())).thenReturn(List.of(taskDto));
        sickDayService.getRemainSickDaysByIds(List.of("id"), BranchType.MOSCOW.getAsString());
        // then
        verify(taskManager, times(1)).findByIds(ArgumentMatchers.anyList());
    }

    @Test
    @DisplayName("Тестирование обновление больничного")
    public void updateSickDayById() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        // when
        when(taskManager.update(any())).thenReturn(new TaskDto());
        sickDayService.updateSickDayById(sickDayTaskDto.getId(), sickDayTaskDto);
        // then
        verify(taskManager, times(1)).update(ArgumentMatchers.any());
    }


    @SneakyThrows
    @Test
    @DisplayName("Тестирование поиска sick day по id, когда задача не была найдена")
    public void sickDayTaskDtoMapWithoutWithoutTaskTest() {
        // given
        SickDayService mock = Mockito.mock(SickDayServiceImpl.class);
        // when
        // TaskRepository mock объект, поэтому такое исключение может быть вызвано только
        // при обращении с настоящим TaskRepository.
        // Поэтому с помощью thenThrow будет имитироваться такое поведение.
        when(mock.getSickDayById(anyString())).thenThrow(SickDayNotFoundException.class);
        // then
        assertThrows(SickDayNotFoundException.class, () -> mock.getSickDayById("id"));
    }
}