package ru.egartech.sickday;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class SickDayApplication {
    public static void main(String[] args) {
        SpringApplication.run(SickDayApplication.class, args);
    }
}
