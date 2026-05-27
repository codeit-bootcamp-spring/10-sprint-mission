package com.sprint.mission.discodeit.config.init;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "discodeit.admin")
@Getter
@Setter
public class AdminProperties {

    private String username;
    private String password;
    private String email;
}
