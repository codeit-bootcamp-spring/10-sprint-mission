package com.sprint.mission.discodeit.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.nio.file.Path;

//@ConfigurationProperties(prefix = "discodeit.storage.local")
public record LocalBCStorageProperties(Path rootPath) {
}
