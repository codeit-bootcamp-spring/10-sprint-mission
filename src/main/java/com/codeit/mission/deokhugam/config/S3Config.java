package com.codeit.mission.deokhugam.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@ConditionalOnProperty(name = "deokhugam.storage.type", havingValue = "S3")
public class S3Config {

  //임시로 세팅, 추후 인프라 세팅 시 변경될 예정
  @Value("${cloud.aws.s3.region:ap-northeast-2}")
  private String region;

  @Value("${cloud.aws.credentials.access-key}")
  private String accessKey;

  @Value("${cloud.aws.credentials.secret-key}")
  private String secretKey;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .region(Region.of(region)) // 서울 리전
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)        // AWS 키 등록
        ))
        .build();
  }

  @Bean(destroyMethod = "close")
  public S3Presigner s3Presigner() {
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();
  }
}