package com.sprint.mission.discodeit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "discodeit.storage.s3")
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String region;
    private Long presignedUrlExpiration;
}