package com.sprint.mission.discodeit.config.aws.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws.s3")
public record AwsProperties(String accessKey,
                            String secretKey,
                            String bucket,
                            String region,
                            Long presignedURLExpiration) {

}
