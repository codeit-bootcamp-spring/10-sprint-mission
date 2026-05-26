package com.sprint.mission.discodeit.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "discodeit.admin")
public record AdminProperties(
    String username,
    String email,
    String password
) {

}
