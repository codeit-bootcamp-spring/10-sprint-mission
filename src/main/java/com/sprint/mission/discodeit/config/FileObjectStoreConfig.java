package com.sprint.mission.discodeit.config;

import com.sprint.mission.discodeit.repository.file.FileObjectStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FileObjectStoreConfig {

    @Bean
    public FileObjectStore store() {
        return FileObjectStore.loadData();
    }
}
