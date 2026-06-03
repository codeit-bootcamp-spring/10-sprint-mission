package com.sprint.mission.discodeit.config.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "discodeit.security.jwt")
@Getter
@Setter
public class JwtProperties {

    private String jwtSecretKey;
    private Duration accessTokenExpirationTime;
    private Duration refreshTokenExpirationTime;
}
