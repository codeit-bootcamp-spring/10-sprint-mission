package com.sprint.mission.discodeit.storage.s3;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@ConfigurationProperties(prefix = "discodeit.storage.s3")
public class S3Properties {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;
    private long presignedUrlExpiration;
}
