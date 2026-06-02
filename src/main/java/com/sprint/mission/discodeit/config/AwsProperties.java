package com.sprint.mission.discodeit.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "aws.s3")
@Getter
@Setter
public class AwsProperties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
}
