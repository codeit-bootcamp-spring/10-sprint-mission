package com.sprint.mission.discodeit.aws;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsS3Config {

  private final AwsProperties props;

  public AwsS3Config(AwsProperties props) {
    this.props = props;
  }

  @Bean
  public S3Client s3Client() {
    if (props.getCredentials().getAccessKey() != null && !props.getCredentials().getAccessKey()
        .isBlank()) {
      return S3Client.builder()
          .region(Region.of(props.getRegion()))
          .credentialsProvider(
              StaticCredentialsProvider.create(
                  AwsBasicCredentials.create(
                      props.getCredentials().getAccessKey(),
                      props.getCredentials().getSecretKey()
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
            (props.getCredentials().getAccessKey() != null && !props.getCredentials().getAccessKey()
                .isBlank())
                ? StaticCredentialsProvider.create(AwsBasicCredentials.create(
                props.getCredentials().getAccessKey(),
                props.getCredentials().getSecretKey()))
                : DefaultCredentialsProvider.create()
        )
        .build();
  }
}