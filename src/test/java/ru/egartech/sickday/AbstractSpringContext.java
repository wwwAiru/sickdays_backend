package ru.egartech.sickday;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import ru.egartech.sdk.api.TaskClient;
import ru.egartech.sdk.api.impl.ListTaskClientImpl;
import ru.egartech.sdk.api.impl.TaskClientImpl;
import ru.egartech.sickday.config.PropertiesTestConfig;
import ru.egartech.sickday.config.RepositoryTestConfig;
import ru.egartech.sickday.config.ServiceTestConfig;
import ru.egartech.sickday.config.DomainTestConfig;
import ru.egartech.sickday.mapper.SickDayMapper;

@SpringBootTest(classes = {
        ObjectMapper.class,
        SickDayMapper.class,
        PropertiesTestConfig.class,
        DomainTestConfig.class,
        ServiceTestConfig.class,
        RepositoryTestConfig.class,
        TaskClient.class,
        TaskClientImpl.class,
        ListTaskClientImpl.class,
        RestTemplate.class
})
public class AbstractSpringContext {
}
