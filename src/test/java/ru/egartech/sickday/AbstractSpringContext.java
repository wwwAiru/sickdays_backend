package ru.egartech.sickday;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(classes = {
        SickDayApplication.class,
        ObjectMapper.class,
})
@ActiveProfiles("test")
public class AbstractSpringContext {
}
