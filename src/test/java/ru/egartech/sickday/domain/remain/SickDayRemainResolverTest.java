package ru.egartech.sickday.domain.remain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.egartech.sickday.AbstractSpringContextClass;
import ru.egartech.sickday.config.SickDayRemainResolverTestConfig;
import ru.egartech.sickday.model.SickDayTaskDto;
import ru.egartech.sickday.util.Generator;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ContextConfiguration(classes = SickDayRemainResolverTestConfig.class)
@DisplayName("Тестирование класса для нахождения количества доступных sick days")
class SickDayRemainResolverTest extends AbstractSpringContextClass {
    @Autowired
    private SickDayRemainResolver sickDayRemainResolver;

    @Nested
    @DisplayName("Тестирование PerYearAggregator")
    class PerYearAggregatorTest {
        private static final FreeSickDayExtraditionType TYPE = FreeSickDayExtraditionType.YEAR;

        @Test
        @DisplayName("Тестирование оставшегося количества sick days, когда было взято больше чем возможно")
        public void moreThanMaximumSickDayCountTest() {
            // given
            List<SickDayTaskDto> sickDays = Generator.generateSickDays(10);

            // when
            long remainSickDaysCount = sickDayRemainResolver.compute(TYPE, sickDays);

            // then
            assertThat(remainSickDaysCount).isEqualTo(0);
        }

        @Test
        @DisplayName("Тестирование оставшегося количества sick days, когда было взято 2")
        public void inRangeMaximumSickDayCountTest() {
            // given
            List<SickDayTaskDto> sickDays = Generator.generateSickDays(2);

            // when
            long remainSickDaysCount = sickDayRemainResolver.compute(TYPE, sickDays);

            // then
            assertThat(remainSickDaysCount).isEqualTo(2);
        }

        @Test
        @DisplayName("Тестирование оставшегося количества sick days, когда не было взято ни одного")
        public void minimumSickDayCountTest() {
            // given
            List<SickDayTaskDto> sickDays = List.of();

            // when
            long remainSickDaysCount = sickDayRemainResolver.compute(TYPE, sickDays);

            // then
            assertThat(remainSickDaysCount).isEqualTo(4);
        }
    }

    @Nested
    @DisplayName("Тестирование PerQuarterAggregator")
    class PerQuarterAggregatorTest {
        private static final FreeSickDayExtraditionType TYPE = FreeSickDayExtraditionType.QUARTER;

        @Test
        @DisplayName("Тестирование оставшегося количества sick days, когда было взято больше чем возможно")
        public void moreThanMaximumSickDayCountTest() {
            // given
            List<SickDayTaskDto> sickDays = Generator.generateSickDays(10);

            // when
            long remainSickDaysCount = sickDayRemainResolver.compute(TYPE, sickDays);

            // then
            assertThat(remainSickDaysCount).isEqualTo(0);
        }

        @Test
        @DisplayName("Тестирование оставшегося количества sick days, когда не было взято ни одного")
        public void minimumSickDayCountTest() {
            // given
            List<SickDayTaskDto> sickDays = List.of();

            // when
            long remainSickDaysCount = sickDayRemainResolver.compute(TYPE, sickDays);

            // then
            assertThat(remainSickDaysCount).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("Тестирование PerWorkAggregator")
    class PerWorkAggregatorTest {

        private static final FreeSickDayExtraditionType TYPE = FreeSickDayExtraditionType.WORK;

        @Test
        @DisplayName("Тестирование оставшегося количества sick days, когда было взято больше чем возможно")
        public void moreThanMaximumSickDayCountTest() {
            // given
            List<SickDayTaskDto> sickDays = Generator.generateSickDays(10);

            // when
            long remainSickDaysCount = sickDayRemainResolver.compute(TYPE, sickDays);

            // then
            assertThat(remainSickDaysCount).isEqualTo(0);
        }

        @Test
        @DisplayName("Тестирование оставшегося количества sick days, когда было взято 2")
        public void inRangeMaximumSickDayCountTest() {
            // given
            List<SickDayTaskDto> sickDays = Generator.generateSickDays(2);

            // when
            long remainSickDaysCount = sickDayRemainResolver.compute(TYPE, sickDays);

            // then
            assertThat(remainSickDaysCount).isEqualTo(2);
        }

        @Test
        @DisplayName("Тестирование оставшегося количества sick days, когда не было взято ни одного")
        public void minimumSickDayCountTest() {
            // given
            List<SickDayTaskDto> sickDays = List.of();

            // when
            long remainSickDaysCount = sickDayRemainResolver.compute(TYPE, sickDays);

            // then
            assertThat(remainSickDaysCount).isEqualTo(4);
        }
    }
}