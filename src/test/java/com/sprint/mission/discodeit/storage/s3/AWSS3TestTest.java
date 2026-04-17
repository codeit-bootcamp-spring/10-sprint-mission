package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class AWSS3TestTest {

  private AWSS3Test awsS3Test;
  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private String bucketName;

  @BeforeEach
  void setUp() throws Exception {
    awsS3Test = new AWSS3Test();

    // env 로드
    Properties props = awsS3Test.loadEnv();

    // 키값
    String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    String regionName = props.getProperty("AWS_S3_REGION");
    this.bucketName = props.getProperty("AWS_S3_BUCKET");

    // 2. S3 클라이언트 및 프리사이너 초기화
    Region region = Region.of(regionName);
    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(credentials);

    this.s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .build();

    this.s3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .build();
  }

  @Test
  @DisplayName("S3 통합 테스트(e2e): 업로드, URL 생성, 다운로드 확인")
  @EnabledIfEnvironmentVariable(named = "RUN_S3_CONNECT_TEST", matches = "true")
//특정 환경에서만 돌리기
  void s3IntegrationTest() {
    //given
    String testKey = "test/hello.txt";
    String content = "Hello S3...!";
    byte[] data = content.getBytes(StandardCharsets.UTF_8);

    // 1. 업로드 테스트
    awsS3Test.upload(s3Client, bucketName, testKey, data);
    System.out.println("1. 업로드 성공");

    // 2. Presigned URL 생성 테스트
    String presignedUrl = awsS3Test.generatePresignedUrl(s3Presigner, bucketName, testKey);
    assertNotNull(presignedUrl);
    assertTrue(presignedUrl.contains(bucketName));
    System.out.println("2. Presigned URL 성공: " + presignedUrl);

    // 3. 다운로드 테스트
    awsS3Test.download(s3Client, bucketName, testKey);
    System.out.println("3. 다운로드 성공");
  }

}