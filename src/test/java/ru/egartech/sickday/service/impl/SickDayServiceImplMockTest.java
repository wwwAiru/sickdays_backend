package ru.egartech.sickday.service.impl;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ContextConfiguration;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.TasksDto;
import ru.egartech.sickday.AbstractSpringContextClass;
import ru.egartech.sickday.config.PropertiesTestConfig;
import ru.egartech.sickday.config.SickDayServiceImplTestConfig;
import ru.egartech.sickday.domain.branch.BranchType;
import ru.egartech.sickday.domain.position.SickDayListIdByPositionResolver;
import ru.egartech.sickday.domain.remain.SickDayRemainResolver;
import ru.egartech.sickday.exception.sickday.SickDayNotFoundException;
import ru.egartech.sickday.mapper.SickDayMapper;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FieldIdsProperties;
import ru.egartech.sickday.repository.TaskRepository;
import ru.egartech.sickday.service.SickDayService;
import ru.egartech.sickday.util.Generator;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {
        SickDayServiceImplTestConfig.class,
        PropertiesTestConfig.class,
})
@DisplayName("Тестирование mock'а SickDayService")
class SickDayServiceImplMockTest extends AbstractSpringContextClass {
    @MockBean
    private SickDayRemainResolver sickDayRemainResolver;

    @MockBean
    private SickDayListIdByPositionResolver sickDayListIdByPositionResolver;

    @MockBean
    private TaskRepository taskRepository;

    @SpyBean
    private SickDayMapper sickDayMapper;

    @Autowired
    private FieldIdsProperties fieldIdsProperties;

    @Autowired
    private SickDayService sickDayService;

    @SneakyThrows
    @Test
    @DisplayName("Тестирование создание нового больничного")
    public void addSickDayTest() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = Generator.generateTasks(1).get(0);

        // when
        when(taskRepository.create(anyInt(), any())).thenReturn(taskDto);
        when(taskRepository.update(any())).thenReturn(new TaskDto());
        sickDayService.addSickDay(sickDayTaskDto);

        // then
        verify(taskRepository, times(1)).create(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        verify(taskRepository, times(1)).update(ArgumentMatchers.any());
    }

    @Test
    @DisplayName("Тестирование поиска оставшихся sick day по egar id")
    public void getRemainSickDaysByEgarIdTest() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = Generator.generateTasks(1).get(0);
        // подставляются валидные, случайно сгенерированные значения в TaskDto,
        // чтобы внутри сервиса не вызывались кастомные исключения
        taskDto.setCustomFields(sickDayMapper.getSickDayTaskDtoShortCustomFields(sickDayTaskDto));
        TasksDto tasksDto = new TasksDto(List.of(taskDto));

        // when
        when(taskRepository.findTasksByCustomFields(anyInt(), any())).thenReturn(tasksDto);
        when(taskRepository.findById(anyString())).thenReturn(Optional.of(taskDto));
        sickDayService.getSickDayByIdAndEgarId("egar_id", "1", 0);

        // then
        verify(taskRepository, times(1))
                .findTasksByCustomFields(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        verify(taskRepository, times(1)).
                findById(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("Тестирование поиска оставшихся sick day по ids ")
    public void getRemainSickDaysByIdsTest() {
        // given
        List<SickDayTaskDto> sickDayTaskDtos = Generator.generateSickDays(3);
        List<TaskDto> taskDtos = Generator.generateTasks(3);
        for (int i = 0; i < sickDayTaskDtos.size(); i++) {
            // подставляются валидные, случайно сгенерированные значения в TaskDto,
            // чтобы внутри сервиса не вызывались кастомные исключения
            TaskDto taskDto = taskDtos.get(i);
            SickDayTaskDto sickDayTaskDto = sickDayTaskDtos.get(i);
            taskDto.setCustomFields(sickDayMapper.getSickDayTaskDtoShortCustomFields(sickDayTaskDto));
        }
        List<String> sickDaysIds = sickDayTaskDtos
                .stream()
                .map(SickDayTaskDto::getId)
                .toList();

        // when
        when(taskRepository.findByIds(anyList())).thenReturn(taskDtos);
        sickDayService.getRemainSickDaysByIds(sickDaysIds, BranchType.MOSCOW.getAsString());

        // then
        verify(taskRepository, times(1)).findByIds(ArgumentMatchers.anyList());
    }

    @Test
    @DisplayName("Тестирование поиска по id больничного")
    public void getSickDayByIdTest() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = Generator.generateTasks(1).get(0);
        // подставляются валидные, случайно сгенерированные значения в TaskDto,
        // чтобы внутри сервиса не вызывались кастомные исключения
        taskDto.setCustomFields(sickDayMapper.getSickDayTaskDtoShortCustomFields(sickDayTaskDto));

        // when
        when(taskRepository.findById(anyString())).thenReturn(Optional.of(taskDto));
        sickDayService.getSickDayById("id");

        // then
        verify(taskRepository, times(1)).findById(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("Тестирование поиска по egar id и id больничного")
    public void getSickDayByIdAndEgarIdTest() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = Generator.generateTasks(1).get(0);
        // подставляются валидные, случайно сгенерированные значения в TaskDto,
        // чтобы внутри сервиса не вызывались кастомные исключения
        taskDto.setCustomFields(sickDayMapper.getSickDayTaskDtoShortCustomFields(sickDayTaskDto));
        TasksDto taskDtos = new TasksDto(List.of(taskDto));

        // when
        when(taskRepository.findTasksByCustomFields(anyInt(), any())).thenReturn(taskDtos);
        when(taskRepository.findById(anyString())).thenReturn(Optional.of(taskDto));
        sickDayService.getSickDayByIdAndEgarId("egar_id", sickDayTaskDto.getId(), 0);

        // then
        verify(taskRepository, times(1))
                .findTasksByCustomFields(ArgumentMatchers.anyInt(), ArgumentMatchers.any());
        verify(taskRepository, times(1))
                .findById(ArgumentMatchers.anyString());
    }

    @Test
    @DisplayName("Тестирование поиска по ids больничных")
    public void getSickDayByIdsTest() {
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);
        TaskDto taskDto = Generator.generateTasks(1).get(0);
        // подставляются валидные, случайно сгенерированные значения в TaskDto,
        // чтобы внутри сервиса не вызывались кастомные исключения
        taskDto.setCustomFields(sickDayMapper.getSickDayTaskDtoShortCustomFields(sickDayTaskDto));

        // when
        when(taskRepository.findByIds(anyList())).thenReturn(List.of(taskDto));
        sickDayService.getRemainSickDaysByIds(List.of("id"), BranchType.MOSCOW.getAsString());

        // then
        verify(taskRepository, times(1)).findByIds(ArgumentMatchers.anyList());
    }

    @Test
    @DisplayName("Тестирование обновление больничного")
    public void updateSickDayById() {
        // given
        SickDayTaskDto sickDayTaskDto = Generator.generateSickDays(1).get(0);

        // when
        when(taskRepository.update(any())).thenReturn(new TaskDto());
        sickDayService.updateSickDayById(sickDayTaskDto.getId(), sickDayTaskDto);

        // then
        verify(taskRepository, times(1)).update(ArgumentMatchers.any());
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