package com.sprint.mission.discodeit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "discodeit.repository")
public class RepositoryProperties {

    private String type = "jcf";
    private String fileDirectory = ".data";
}
