package com.sprint.mission.discodeit.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3Config {

  @Bean(destroyMethod = "close")
  public S3Client s3Client(StorageProperties storageProperties) {
    StorageProperties.S3 s3 = storageProperties.getS3();
    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        s3.getAccessKey(),
        s3.getSecretKey()
    );

    return S3Client.builder()
        .region(Region.of(s3.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .serviceConfiguration(S3Configuration.builder().build())
        .build();
  }

  @Bean(destroyMethod = "close")
  public S3Presigner s3Presigner(StorageProperties storageProperties) {
    StorageProperties.S3 s3 = storageProperties.getS3();
    AwsBasicCredentials credentials = AwsBasicCredentials.create(
        s3.getAccessKey(),
        s3.getSecretKey()
    );

    return S3Presigner.builder()
        .region(Region.of(s3.getRegion()))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }
}
