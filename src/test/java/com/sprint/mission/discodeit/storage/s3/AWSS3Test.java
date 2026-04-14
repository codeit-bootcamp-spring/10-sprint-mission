package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

public class AWSS3Test {

  private S3Client s3Client; // 업로드, 다운로드용 S3 클라이언트
  private S3Presigner s3Presigner; // 임시 다운로드 URL 생성용 Presigner
  private String bucketName;

  @BeforeEach
  void setUp() {
    // 1. 환경 변수 설정 세팅
    String accessKey = System.getenv("AWS_S3_ACCESS_KEY");
    String secretKey = System.getenv("AWS_S3_SECRET_KEY");
    String regionStr = System.getenv("AWS_S3_REGION");
    bucketName = System.getenv("AWS_S3_BUCKET");

    // 2. 자격 증명 및 리전 설정
    StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
        AwsBasicCredentials.create(accessKey, secretKey)
    );
    Region region = Region.of(regionStr);

    // 3. S3 클라이언트 및 Presigner 객체 생성
    s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .build();

    s3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(credentialsProvider)
        .build();
  }

  @Test
  void uploadTest() {
    // given
    String key = "test-dir/Discodeit-S3-test.txt";
    String content = "Discodeit S3 test";

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    // when
    // S3에 문자열 데이터 업로드
    var response = s3Client.putObject(putObjectRequest, RequestBody.fromString(content));

    // then
    assertThat(response.sdkHttpResponse().isSuccessful()).isTrue();
  }

  @Test
  void downloadTest() {
    // given
    String key = "test-dir/Discodeit-S3-test.txt";

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    // when
    // S3에서 데이터 다운로드 후 문자열로 변환
    String downloadedContent = s3Client.getObjectAsBytes(getObjectRequest).asUtf8String();

    // then
    assertThat(downloadedContent).isEqualTo("Discodeit S3 test");
  }

  @Test
  void presignedUrlTest() {
    // given
    String key = "test-dir/Discodeit-S3-test.txt";

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    // when
    // 10분 동안 유효한 임시 다운로드 URL 생성
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    String url = presignedRequest.url().toString();

    // then
    assertThat(url).isNotBlank();
  }
}
