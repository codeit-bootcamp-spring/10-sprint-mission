package com.sprint.mission.discodeit.global.infra.storage.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record AwsProperties(String accessKey,
                            String secretKey,
                            String bucket,
                            String region,
                            Long presignedURLExpiration) {

}
