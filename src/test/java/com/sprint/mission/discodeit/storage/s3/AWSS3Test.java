package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.config.AwsProperties;
import java.time.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@SpringJUnitConfig
@TestPropertySource(locations = "file:.env")
class AWSS3Test {

    @Value("${AWS_S3_ACCESS_KEY}")
    private String accessKey;

    @Value("${AWS_S3_SECRET_KEY}")
    private String secretKey;

    @Value("${AWS_S3_REGION}")
    private String region;

    @Value("${AWS_S3_BUCKET}")
    private String bucket;

    private S3Client s3Client;
    private AwsBasicCredentials credentials;

    @BeforeEach
    void setUp() {
        credentials = AwsBasicCredentials.create(
                accessKey, secretKey);

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @Test
    @DisplayName("업로드: 예외 발생 없이 실행되는지 확인")
    void uploadTest() {
        // Given
        String key = "test-upload.txt";
        String content = "Upload Test Content";

        // When & Then
        assertDoesNotThrow(() -> {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();
            s3Client.putObject(putOb, RequestBody.fromString(content));
        }, "S3 업로드 중 예외가 발생하지 않아야 합니다.");
    }

    @Test
    @DisplayName("다운로드: 업로드된 내용과 일치하는지 검증")
    void downloadTest() {
        // Given
        String key = "test-download.txt";
        String expectedContent = "Hello S3 Download! " + java.util.UUID.randomUUID();

        // 업로드
        s3Client.putObject(PutObjectRequest.builder()
                .bucket(bucket)
                .key(key).build(), RequestBody.fromString(expectedContent));

        // When
        GetObjectRequest getOb = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getOb);
        String actualContent = objectBytes.asUtf8String();

        // Then
        assertNotNull(objectBytes, "다운로드된 객체는 null일 수 없습니다.");
        assertEquals(expectedContent, actualContent, "다운로드한 내용이 업로드한 내용과 일치해야 합니다.");
    }

    @Test
    @DisplayName("PresignedUrl 생성: URL 형식이 올바른지 검증")
    void presignedUrlTest() {
        // When
        PresignedGetObjectRequest presignedRequest;
        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key("test-file.txt")
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            presignedRequest = presigner.presignGetObject(presignRequest);
        }

        // Then
        assertNotNull(presignedRequest, "PresignedGetObjectRequest 객체가 생성되어야 합니다.");
        assertNotNull(presignedRequest.url(), "생성된 Presigned URL은 null일 수 없습니다.");
        assertTrue(presignedRequest.url().toString().startsWith("https://"), "URL은 https로 시작해야 합니다.");
        assertTrue(presignedRequest.url().toString().contains(bucket), "URL에 버킷명이 포함되어야 합니다.");

        System.out.println("검증된 Presigned URL: " + presignedRequest.url());
    }
}
