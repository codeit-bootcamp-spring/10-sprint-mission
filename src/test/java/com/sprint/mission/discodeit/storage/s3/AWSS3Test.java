package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@DisabledIfEnvironmentVariable(named = "GITHUB_ACTIONS", matches = "true")
@ActiveProfiles("test")
public class AWSS3Test {

  private static String accessKey;
  private static String secretKey;
  private static String region;
  private static String bucket;
  private static long presignedUrlExpiration;

  private static S3Client s3Client;
  private static S3Presigner s3Presigner;

  private String key;

  @BeforeAll
  static void SetUp() throws IOException {
    Properties props = new Properties();
    try (FileReader reader = new FileReader(".env")) {
      props.load(reader);
    }

    accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    region = props.getProperty("AWS_S3_REGION");
    bucket = props.getProperty("AWS_S3_BUCKET");
    presignedUrlExpiration = Long.parseLong(props.getProperty("AWS_S3_PRESIGNED_URL_EXPIRATION"));

    s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(getCredentialsProvider())
        .build();
    s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(getCredentialsProvider())
        .build();
  }

  @AfterEach
  void dataDelete() {
    try {
      s3Client.deleteObject(req -> req.bucket(bucket).key(key));
    } catch (RuntimeException e) {
      throw new RuntimeException(key + "테스트 데이터 삭제 에러", e);
    }
  }

  @Test
  void upload() {
    // given
    key = "upload test-" + UUID.randomUUID();
    String content = "upload body";

    // when, then
    assertDoesNotThrow(() ->
        s3Client.putObject(req -> req.bucket(bucket).key(key), RequestBody.fromString(content)));
  }

  @Test
  void download() {
    // given
    key = "download test-" + UUID.randomUUID();
    String content = "download body";

    try {
      s3Client.putObject(req -> req.bucket(bucket).key(key), RequestBody.fromString(content));
    } catch (RuntimeException e) {
      throw new RuntimeException("업로드 에러", e);
    }

    // when
    String downloaded = assertDoesNotThrow(() ->
        s3Client.getObject(req -> req.bucket(bucket).key(key), ResponseTransformer.toBytes())
            .asString(StandardCharsets.UTF_8));

    // then
    assertEquals(content, downloaded);
  }

  @Test
  void generatedPresignedUrl() {
    // given
    key = "presignedUrl test-" + UUID.randomUUID();
    String content = "presignedUrl body";

    try {
      s3Client.putObject(req -> req.bucket(bucket).key(key), RequestBody.fromString(content));
    } catch (RuntimeException e) {
      throw new RuntimeException("업로드 에러", e);
    }

    // when, then
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    PresignedGetObjectRequest presignedRequest = assertDoesNotThrow(() ->
        s3Presigner.presignGetObject(req -> req.getObjectRequest(request)
            .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))));

    System.out.println("Presigned URL: " + presignedRequest.url().toString());
  }

  private static AwsCredentialsProvider getCredentialsProvider() {
    return accessKey != null && !accessKey.isBlank()
        // 수동탐색 방식: (.env, yaml 설정파일 사용)
        ? StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey))
        // 자동탐색 방식: (Java 시스템 속성 -> 환경 변수 -> 자격 증명 파일(AWS CLI 설정값) -> 컨테이너/EC2(IAM ROLE))
        : DefaultCredentialsProvider.create();
  }
}
