package ru.egartech.sickday.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import ru.egartech.sdk.dto.task.deserialization.TaskDto;
import ru.egartech.sdk.dto.task.deserialization.customfield.field.relationship.RelationshipFieldDto;
import ru.egartech.sickday.AbstractTest;
import ru.egartech.sickday.domain.type.SickDayType;
import ru.egartech.sickday.exception.employee.EmployeeNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayDateNotFoundException;
import ru.egartech.sickday.exception.sickday.SickDayTypeNotFoundException;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.property.FieldIdsProperties;
import ru.egartech.sickday.property.FileNamesProperties;

import java.io.File;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Тестирование маппера")
class SickDayMapperTest extends AbstractTest {
    @Autowired
    private FieldIdsProperties fieldIdsProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private FileNamesProperties fileNamesProperties;

    @SpyBean
    private TaskMapper sickDayMapper;

    @Nested
    @DisplayName("Корректные данные")
    class CorrectData {
        @SneakyThrows
        @Test
        @DisplayName("Тестирование корректности преобразования TaskDto больничного в SickDayTaskDto")
        public void sickDayTaskDtoMapTest() {
            // given
            File sickDayTaskJson = FileNamesProperties.getFileFromResources(fileNamesProperties.getSickDayTask());
            TaskDto taskDto = objectMapper.readValue(sickDayTaskJson, TaskDto.class);
            SickDayTaskDto sickDayTaskDto;
            // when
            sickDayTaskDto = sickDayMapper.toDto(taskDto);
            // then
            assertThat(sickDayTaskDto).isNotNull();
            assertThat(sickDayTaskDto)
                    .extracting(SickDayTaskDto::getId)
                    .isEqualTo("2zfy982");

            assertThat(sickDayTaskDto)
                    .extracting(SickDayTaskDto::getName)
                    .isEqualTo("Больничный: Николай Игорь Алексеевич 15.09.2022");

            assertThat(sickDayTaskDto)
                    .extracting(t -> SickDayType.valueOf(t.getType()).getOrderindex())
                    .isEqualTo(0);

            assertThat(sickDayTaskDto)
                    .extracting(SickDayTaskDto::getStartDate)
                    .isNotNull()
                    .isEqualTo("1663117200000");

            assertThat(sickDayTaskDto)
                    .extracting(SickDayTaskDto::getEndDate)
                    .isNotNull()
                    .isEqualTo("1663117200000");
        }
    }

    @Nested
    @DisplayName("Некорректные данные")
    class IncorrectData {
        @SneakyThrows
        @Test
        @DisplayName("Тестирование корректности преобразования TaskDto больничного в SickDayTaskDto" +
                " при отсутствии типа больничного")
        public void sickDayTaskDtoMapWithoutTypeTest() {
            // given
            File sickDayTaskJson = FileNamesProperties.getFileFromResources(fileNamesProperties.getSickDayTask());
            TaskDto taskDto = objectMapper.readValue(sickDayTaskJson, TaskDto.class);
            // подставляются некорректные данные
            taskDto.customField(fieldIdsProperties.getSickDaysType()).setValue(null);
            // when
            Supplier<SickDayTaskDto> action = () -> sickDayMapper.toDto(taskDto);
            // then
            assertThrows(SickDayTypeNotFoundException.class, action::get);
        }

        @SneakyThrows
        @Test
        @DisplayName("Тестирование корректности преобразования TaskDto больничного в SickDayTaskDto" +
                " при отсутствии даты начала или даты конца больничного")
        public void sickDayTaskDtoMapWithoutDateTest() {
            // given
            File sickDayTaskJson = FileNamesProperties.getFileFromResources(fileNamesProperties.getSickDayTask());
            TaskDto taskDtoWithoutStartDate = objectMapper.readValue(sickDayTaskJson, TaskDto.class);
            TaskDto taskDtoWithoutEndDate = objectMapper.readValue(sickDayTaskJson, TaskDto.class);
            // подставляются некорректные данные
            taskDtoWithoutStartDate.customField(fieldIdsProperties.getStartDateId()).setValue(null);
            taskDtoWithoutEndDate.customField(fieldIdsProperties.getEndDateId()).setValue(null);
            // when
            Supplier<SickDayTaskDto> action1 = () -> sickDayMapper.toDto(taskDtoWithoutStartDate);
            Supplier<SickDayTaskDto> action2 = () -> sickDayMapper.toDto(taskDtoWithoutEndDate);
            // then
            assertAll(
                    () -> assertThrows(SickDayDateNotFoundException.class, action1::get),
                    () -> assertThrows(SickDayDateNotFoundException.class, action2::get)
            );
        }

        @SneakyThrows
        @Test
        @DisplayName("Тестирование корректности преобразования TaskDto больничного в SickDayTaskDto" +
                " при отсутствии связи болничного с сотрудником")
        public void sickDayTaskDtoMapWithoutEmployeeRelationshipTest() {
            // given
            File sickDayTaskJson = FileNamesProperties.getFileFromResources(fileNamesProperties.getSickDayTask());
            TaskDto taskDtoWithoutSickDays = objectMapper.readValue(sickDayTaskJson, TaskDto.class);
            // подставляются некорректные данные
            taskDtoWithoutSickDays.<RelationshipFieldDto>customField(fieldIdsProperties.getSickDaysId()).setValue(List.of());
            // when
            Supplier<SickDayTaskDto> action1 = () -> sickDayMapper.toDto(taskDtoWithoutSickDays);
            // then
            assertThrows(EmployeeNotFoundException.class, action1::get);
        }
    }
}