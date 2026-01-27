package com.sprint.mission.discodeit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class PathConfig {

    @Bean
    public Path baseDir() {
        return Paths.get(System.getProperty("user.dir"), "data");
    }
}
