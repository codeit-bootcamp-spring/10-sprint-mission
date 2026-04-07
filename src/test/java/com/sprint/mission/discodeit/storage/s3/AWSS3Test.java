package com.sprint.mission.discodeit.storage.s3;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

class AWSS3Test {

  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private String bucket;

  @BeforeEach
  void setUp() throws IOException {
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      props.load(fis);
    }
    String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    String region = props.getProperty("AWS_S3_REGION");
    bucket = props.getProperty("AWS_S3_BUCKET");

    s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                accessKey,
                secretKey
            )
        ))
        .build();

    s3Presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                accessKey,
                secretKey
            )
        ))
        .build();
  }

  // 업로드
  @Test
  @DisplayName("S3 버킷에 파일 업로드를 성공해야 한다.")
  void should_upload_in_s3_bucket() {
    // given
    String key = "test/" + UUID.randomUUID();
    byte[] fakeImageBytes = "가짜 이미지 데이터".getBytes();

    // when
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fakeImageBytes));

    // then
    HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();
    assertDoesNotThrow(() -> s3Client.headObject(headObjectRequest));
  }

  @Test
  @DisplayName("Presigned URL 생성에 성공해야 한다.")
  void should_download_in_s3_bucket_when_use_presigned_url() {
    // given
    String key = "test/" + UUID.randomUUID();
    String contentType = "image/png";
    String fileName = "test.png";

    // when
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType)
        .responseContentDisposition("attachment; filename=\"" + fileName + "\"")
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    String url = s3Presigner.presignGetObject(presignRequest).url().toString();

    // then
    assertNotNull(url);
    assertTrue(url.contains(bucket));
    assertTrue(url.contains(key));
  }
}