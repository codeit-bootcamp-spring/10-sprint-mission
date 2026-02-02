package com.sprint.mission.discodeit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.repository")
@Getter
@Setter
public class RepositoryProperties {

    private String type = "jcf"; // 기본값
    private String fileDirectory;
}
