package ru.egartech.sickday.config;

import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import ru.egartech.sickday.domain.position.SickDayListIdByPositionResolver;
import ru.egartech.sickday.domain.remain.SickDayRemainResolver;
import ru.egartech.sickday.mapper.SickDayMapper;
import ru.egartech.sickday.property.FieldIdsProperties;
import ru.egartech.sickday.repository.TaskRepository;
import ru.egartech.sickday.repository.impl.TaskRepositoryImpl;
import ru.egartech.sickday.service.SickDayService;
import ru.egartech.sickday.service.impl.SickDayServiceImpl;

@ContextConfiguration(classes = {
        TaskRepositoryImpl.class,
        PropertiesTestConfig.class,
        SickDayRemainResolver.class,
        SickDayListIdByPositionResolver.class,
        SickDayMapperTestConfig.class,
})
public class SickDayServiceImplTestConfig extends AbstractBaseConfig {
    @Bean
    public SickDayService sickDayService(TaskRepository taskRepository,
                                         SickDayRemainResolver sickDayRemainResolver,
                                         SickDayListIdByPositionResolver sickDayListIdByPositionResolver,
                                         FieldIdsProperties fieldIdsProperties,
                                         SickDayMapper sickDayMapper) {
        return new SickDayServiceImpl(
                taskRepository,
                sickDayRemainResolver,
                sickDayListIdByPositionResolver,
                fieldIdsProperties,
                sickDayMapper
        );
    }
}
