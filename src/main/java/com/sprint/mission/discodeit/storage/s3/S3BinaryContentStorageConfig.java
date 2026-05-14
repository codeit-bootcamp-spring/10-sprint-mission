package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3BinaryContentStorageConfig {

    @ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
    public BinaryContentStorage s3BinaryContentStorage() {
        return new S3BinaryContentStorage(
                System.getenv("AWS_S3_ACCESS_KEY"),
                System.getenv("AWS_S3_SECRET_KEY"),
                System.getenv("AWS_S3_REGION"),
                System.getenv("AWS_S3_BUCKET")
        );
    }
}