package com.sprint.mission.discodeit.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Disabled("AWS S3 환경 설정이 필요한 테스트이므로 로컬 빌드에서 제외")
public class AWSS3Test {

  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private String bucket;

  @BeforeEach
  void setUp() throws Exception {
    // env 파일 로드
    Properties properties = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      properties.load(fis);
    }

    // env 값 가져오기
    String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
    String region = properties.getProperty("AWS_S3_REGION");
    bucket = properties.getProperty("AWS_S3_BUCKET");

    // AWS 인증 정보 생성
    AwsBasicCredentials credentials =
        AwsBasicCredentials.create(accessKey, secretKey);

    // S3Client 생성
    s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    // S3Presigner 생성
    s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  @Test
  void upload_success() {
    // given
    String key = "test/" + UUID.randomUUID() + ".txt";
    String content = "hello s3";

    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType("text/plain")
        .build();

    // when
    PutObjectResponse response = s3Client.putObject(
        request,
        RequestBody.fromBytes(content.getBytes(StandardCharsets.UTF_8))
    );

    // then
    assertThat(response.eTag()).isNotBlank();
  }

  @Test
  void download_success() {
    // given
    String key = "test/" + UUID.randomUUID() + ".txt";
    String content = "hello s3";

    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType("text/plain")
        .build();

    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    // when
    s3Client.putObject(
        putRequest,
        RequestBody.fromBytes(content.getBytes(StandardCharsets.UTF_8))
    );

    ResponseBytes<GetObjectResponse> downloadedObject =
        s3Client.getObjectAsBytes(getRequest);
    String downloadedContent = downloadedObject.asUtf8String();

    //then
    assertThat(downloadedContent).isEqualTo(content);
  }

  @Test
  void create_presigned_url_success() {
    // given
    String key = "test/" + UUID.randomUUID() + ".txt";
    String content = "hello s3";

    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .contentType("text/plain")
        .build();

    // when
    s3Client.putObject(
        putRequest,
        RequestBody.fromBytes(content.getBytes(StandardCharsets.UTF_8))
    );
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest =
        s3Presigner.presignGetObject(getObjectPresignRequest);

    String presignedUrl = presignedRequest.url().toString();

    // then
    assertThat(presignedUrl).isNotBlank();
  }
}
