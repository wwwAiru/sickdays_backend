package ru.egartech.sickday.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;

@Data
@TestPropertySource("classpath:application.yml")
@ConfigurationProperties(prefix = "file-names")
@Component
public class FileNamesProperties {
    private final static String FOLDER_NAME = "sick-days-response";

    private String sickDayTask;

    public static File getFileFromResources(String name) throws FileNotFoundException {
        return ResourceUtils.getFile(ResourceUtils.CLASSPATH_URL_PREFIX
                .concat(FOLDER_NAME)
                .concat("/")
                .concat(name));
    }
}
