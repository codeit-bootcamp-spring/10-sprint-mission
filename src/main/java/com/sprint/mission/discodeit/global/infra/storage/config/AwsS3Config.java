package com.sprint.mission.discodeit.global.infra.storage.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@RequiredArgsConstructor
@Configuration
@EnableConfigurationProperties(AwsProperties.class)
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class AwsS3Config {

  @Delegate
  private final AwsProperties properties;

  @Bean
  public S3Client s3Client() {
    if (accessKey().isEmpty()) {
      return S3Client.builder()
          .region(Region.of(region()))
          .credentialsProvider(DefaultCredentialsProvider.builder().build())
          .build();
    }
    return S3Client.builder()
        .region(Region.of(region()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey(), secretKey())
            ))
        .build();
  }

  @Bean
  public S3Presigner s3Presigner() {
    if (accessKey().isEmpty()) {
      return S3Presigner.builder()
          .region(Region.of(region()))
          .credentialsProvider(DefaultCredentialsProvider.builder().build())
          .build();
    }
    return S3Presigner.builder()
        .region(Region.of(region()))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey(), secretKey())
            ))
        .build();
  }
}
