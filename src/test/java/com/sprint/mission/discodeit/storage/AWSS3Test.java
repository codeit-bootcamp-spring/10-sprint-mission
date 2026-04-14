package com.sprint.mission.discodeit.storage;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class AWSS3Test {

    private final Properties properties = loadEnvProperties();

    private final String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
    private final String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
    private final String region = properties.getProperty("AWS_S3_REGION");
    private final String bucket = properties.getProperty("AWS_S3_BUCKET");

    private final S3Client s3Client = createS3Client();
    private final S3Presigner s3Presigner = createS3Presigner();

    @AfterEach
    void tearDown() {
        s3Presigner.close();
        s3Client.close();
    }

    @Test
    @DisplayName("S3 업로드 테스트")
    void upload() {
        // given
        String key = "test/" + UUID.randomUUID() + "-upload.txt";
        String content = "S3 업로드 테스트";

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("text/plain")
                .build();

        // when
        s3Client.putObject(request, RequestBody.fromString(content, StandardCharsets.UTF_8));

        // then
        try (ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        )) {
            String uploadedContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            assertNotNull(uploadedContent);
            assertEqualsText(content, uploadedContent);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            deleteTestObject(key);
        }
    }

    @Test
    @DisplayName("S3 다운로드 테스트")
    void download() {
        // given
        String key = "test/" + UUID.randomUUID() + "-download.txt";
        String expectedContent = "S3 다운로드 테스트";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromString(expectedContent, StandardCharsets.UTF_8)
        );

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // when
        String downloadedContent;
        try (ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(request)) {
            downloadedContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // then
        assertNotNull(downloadedContent);
        assertFalse(downloadedContent.isBlank());
        assertEqualsText(expectedContent, downloadedContent);

        deleteTestObject(key);
    }

    @Test
    @DisplayName("S3 Presigned URL 생성 테스트")
    void createPresignedUrl() {
        // given
        String key = "test/" + UUID.randomUUID() + "-presigned.txt";
        String content = "S3 Presigned URL 생성 테스트";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromString(content, StandardCharsets.UTF_8)
        );

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        // when
        URL presignedUrl = s3Presigner.presignGetObject(presignRequest).url();

        // then
        assertNotNull(presignedUrl);
        assertTrue(presignedUrl.toString().contains(key));
        assertTrue(presignedUrl.toString().startsWith("https://"));

        deleteTestObject(key);
    }

    private Properties loadEnvProperties() {
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(Path.of(".env"))) {
            properties.load(inputStream);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(".env 파일을 읽을 수 없습니다.", e);
        }
    }

    private S3Client createS3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    private S3Presigner createS3Presigner() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    private void deleteTestObject(String key) {
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .build()
        );
    }

    private void assertEqualsText(String expected, String actual) {
        assertTrue(expected.equals(actual),
                () -> "expected: " + expected + ", actual: " + actual);
    }
}
