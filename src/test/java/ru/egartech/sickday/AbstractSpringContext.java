package ru.egartech.sickday;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {
        SickDayApplication.class,
        ObjectMapper.class,
})
public class AbstractSpringContext {
}
