package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

class AWSs3TEST {

  String accessKey;
  String secretKey;
  String region;
  String bucket;
  S3Client s3;

  @BeforeEach
  void setUp() throws Exception {
    Properties props = new Properties();
    props.load(new FileInputStream(".env"));
    accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
    secretKey = props.getProperty("AWS_S3_SECRET_KEY");
    region = props.getProperty("AWS_S3_REGION");
    bucket = props.getProperty("AWS_S3_BUCKET");

    s3 = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();
  }

  @Test
  @DisplayName("업로드 테스트")
  void uploadTest() {
    s3.putObject(
        PutObjectRequest.builder()
            .bucket(bucket)
            .key("test/test.txt")
            .build(),
        RequestBody.fromString("hello s3!")
    );

  }

  @Test
  @DisplayName("다운로드 테스트")
  void downloadTest() {
    String content = s3.getObjectAsBytes(
        GetObjectRequest.builder()
            .bucket(bucket)
            .key("test/test.txt")
            .build()
    ).asUtf8String();

    assertEquals("hello s3!", content);
  }

  @Test
  @DisplayName("PresignedUrl 생성 테스트")
  void presignedUrlTest() {
    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();

    String url = presigner.presignGetObject(
        GetObjectPresignRequest.builder()
            .getObjectRequest(r -> r.bucket(bucket).key("test/test.txt"))
            .signatureDuration(Duration.ofMinutes(10))
            .build()
    ).url().toString();

    assertNotNull(url);
    System.out.println("Presigned URL: " + url);
  }

}
