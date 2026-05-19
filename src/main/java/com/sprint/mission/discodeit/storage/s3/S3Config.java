package com.sprint.mission.discodeit.storage.s3;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3Config {

    private final AwsProperties props;

    public S3Config(AwsProperties props) {
        this.props = props;
    }

    @Bean
    public S3Client s3Client() {
        if (hasStaticCredentials()) {
            return S3Client.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(
                    StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(
                            props.getAccessKey(),
                            props.getSecretKey()
                        )
                    )
                )
                .build();
        }

        return S3Client.builder()
            .region(Region.of(props.getRegion()))
            .credentialsProvider(DefaultCredentialsProvider.create())
            .build();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(props.getRegion()))
            .credentialsProvider(
                hasStaticCredentials()
                    ? StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        props.getAccessKey(),
                        props.getSecretKey()
                    )
                )
                    : DefaultCredentialsProvider.create()
            )
            .build();
    }

    @Bean
    public S3BinaryContentStorage s3BinaryContentStorage() {
        return new S3BinaryContentStorage(
            props.getBucket(),
            s3Client(),
            s3Presigner()
        );
    }

    private boolean hasStaticCredentials() {
        return props.getAccessKey() != null
            && !props.getAccessKey().isBlank()
            && props.getSecretKey() != null
            && !props.getSecretKey().isBlank();
    }
}
