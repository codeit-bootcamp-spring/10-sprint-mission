package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AWSS3Test {

  private static S3Client s3Client;
  private static S3Presigner s3Presigner;
  private static String bucketName;

  @BeforeAll
  static void setUp() {
    Properties props = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      props.load(fis);
    } catch (IOException e) {
      // .env 파일이 없는 경우(CI 환경) 로그를 남기지 않고 시스템 환경 변수 사용으로 넘어갑니다.
    }

    String accessKey = getProp(props, "AWS_ACCESS_KEY_ID");
    String secretKey = getProp(props, "AWS_SECRET_ACCESS_KEY");
    String regionStr = getProp(props, "AWS_REGION");
    bucketName = getProp(props, "AWS_BUCKET_NAME");

    AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
    Region region = Region.of(regionStr);

    s3Client = S3Client.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    s3Presigner = S3Presigner.builder()
        .region(region)
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();
  }

  private static String getProp(Properties props, String key) {
    String value = props.getProperty(key);
    return (value != null) ? value : System.getenv(key);
  }

  @Test
  void testUpload() {
    String key = "test/test-upload.txt";
    String content = "Hello Upload!";

    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    PutObjectResponse response = s3Client.putObject(putObjectRequest,
        RequestBody.fromString(content));

    assertNotNull(response);
  }

  @Test
  void testDownload() {
    String key = "test/test-download.txt";
    String content = "Hello Download!";

    s3Client.putObject(
        PutObjectRequest.builder().bucket(bucketName).key(key).build(),
        RequestBody.fromString(content)
    );

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
    String downloadedContent = responseBytes.asUtf8String();

    assertEquals(content, downloadedContent);
  }

  @Test
  void testGeneratePresignedUrl() {
    String key = "test/test-presigned.txt";

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucketName)
        .key(key)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofMinutes(10))
        .getObjectRequest(getObjectRequest)
        .build();

    PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
    String url = presignedRequest.url().toString();

    assertNotNull(url);
    assertTrue(url.contains("X-Amz-Signature"));
    assertTrue(url.contains(bucketName));
  }
}
