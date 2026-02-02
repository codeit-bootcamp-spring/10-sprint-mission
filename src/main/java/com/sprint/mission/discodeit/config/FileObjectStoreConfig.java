package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.file.FileObjectStore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Configuration
@ConfigurationProperties(prefix = "discodeit.repository")
@Getter
@Setter
public class FileObjectStoreConfig {
    private Path fileDirectory;

    @Bean
    public FileObjectStore store() {
        return FileObjectStore.loadData(fileDirectory);
    }
}
