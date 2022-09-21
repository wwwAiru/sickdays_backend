package ru.egartech.sickday.config;

import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import ru.egartech.sickday.mapper.SickDayMapper;
import ru.egartech.sickday.property.FieldIdsProperties;

@ContextConfiguration(classes = {
        SickDayMapper.class,
        PropertiesTestConfig.class,
})
public class SickDayMapperTestConfig extends AbstractBaseConfig {
    @Bean
    public SickDayMapper sickDayMapper(FieldIdsProperties fieldIdsProperties) {
        return new SickDayMapper(fieldIdsProperties);
    }
}
