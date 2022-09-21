package ru.egartech.sickday.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import ru.egartech.sickday.property.FieldIdsProperties;
import ru.egartech.sickday.property.FileNamesProperties;
import ru.egartech.sickday.property.ListIdsProperties;

@ContextConfiguration(classes = {
        FieldIdsProperties.class,
        ListIdsProperties.class,
})
@EnableConfigurationProperties({
        FileNamesProperties.class,
})
public class PropertiesTestConfig {
    @Bean
    public FieldIdsProperties fieldIdsProperties() {
        return new FieldIdsProperties();
    }

    @Bean
    public ListIdsProperties listIdsProperties() {
        return new ListIdsProperties();
    }

    @Bean
    public FileNamesProperties fileNamesProperties() {
        return new FileNamesProperties();
    }
}
