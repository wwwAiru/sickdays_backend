package ru.egartech.sickday.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import ru.egartech.sdk.property.SearchListsProperties;
import ru.egartech.sickday.property.FileNamesProperties;

@ComponentScan("ru.egartech.sickday.property")
@Import(SearchListsProperties.class)
@EnableConfigurationProperties({FileNamesProperties.class})
public class PropertiesTestConfig extends AbstractBaseConfig {
    @Bean
    public FileNamesProperties fileNamesProperties() {
        return new FileNamesProperties();
    }
}
