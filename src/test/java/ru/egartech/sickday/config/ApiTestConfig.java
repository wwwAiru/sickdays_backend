package ru.egartech.sickday.config;

import org.springframework.test.context.ContextConfiguration;
import ru.egartech.sdk.config.TaskClientAutoConfiguration;

@ContextConfiguration(classes = {
        TaskClientAutoConfiguration.class,
})
public class ApiTestConfig extends AbstractBaseConfig {
}
