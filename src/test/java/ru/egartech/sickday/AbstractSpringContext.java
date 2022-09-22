package ru.egartech.sickday;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import ru.egartech.sdk.api.TaskClient;
import ru.egartech.sdk.api.impl.ListTaskClientImpl;
import ru.egartech.sdk.api.impl.TaskClientImpl;
import ru.egartech.sickday.config.*;

@SpringBootTest(classes = {
        ObjectMapper.class,
        MappersTestConfig.class,
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
