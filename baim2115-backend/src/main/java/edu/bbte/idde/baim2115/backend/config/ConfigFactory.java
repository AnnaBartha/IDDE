package edu.bbte.idde.baim2115.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class ConfigFactory {
    private static Config instance;
    private static final Logger log = LoggerFactory.getLogger(ConfigFactory.class);

    public static synchronized Config getConfig() {
        String envVariable = System.getenv("PROFILE");
        if (envVariable == null) {
            envVariable = "inmemory";
        }
        String fileName = "/application-" + envVariable + ".yaml";
        log.info(fileName);
        log.info("BENT AZ OLVASASBAN");
        if (instance == null) {
            log.info("BENT AZ IFBEN");
            // beolvasom a .yaml filet
            InputStream inputStream = Config.class.getResourceAsStream(fileName);
            // convertalom .java class-e
            ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
            try {
                log.info("TRY ELERVE");
                instance = objectMapper.readValue(inputStream, Config.class);
            } catch (IOException e) {
                log.error("AJAJ");
                instance = new Config();
                instance.setProfile("memory");
            }
        }
        log.info("vege" + instance);
        return instance;
    }
}
