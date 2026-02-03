package com.sprint.mission.discodeit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@EnableConfigurationProperties(RepositoryProperties.class)
@ConditionalOnProperty(prefix = "discodeit.repository", name = "type", havingValue = "file")
public class PathConfig {

    @Bean
    public Path baseDir(RepositoryProperties properties) {
        Path dir = Paths.get(properties.getFileDirectory());
        if (dir.isAbsolute()) {
            return dir.normalize();
        }

        return Paths.get(System.getProperty("user.dir"))
                .resolve(dir)
                .normalize();
    }
}
