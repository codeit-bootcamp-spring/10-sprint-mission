package com.sprint.mission.discodeit.storage.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@ConditionalOnProperty(
        prefix = "discodeit.storage",
        name = "type",
        havingValue = "s3"
)
@EnableConfigurationProperties(S3StorageProperties.class)
public class S3Config {

    private final S3StorageProperties props;

    public S3Config(S3StorageProperties props) {
        this.props = props;
    }

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                         props.getAccessKey(),
                        props.getSecretKey()
        );

        return S3Client.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(awsBasicCredentials)
                ).build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        AwsBasicCredentials awsBasicCredentials = AwsBasicCredentials.create(
                props.getAccessKey(),
                props.getSecretKey()
        );

        return S3Presigner.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                        StaticCredentialsProvider.create(awsBasicCredentials)
                ).build();
    }
}
