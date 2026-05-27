package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@Disabled // s3 미연결 시 오류 발생하지 않게 설정
@DisplayName("AWS S3 연결 확인 테스트(실제 파일 사용 X)")
public class AWSS3Test {

    private static S3Client s3Client;
    private static S3Presigner s3Presigner;

    private static String accessKey;
    private static String secretKey;
    private static String region;
    private static String bucket;

    @BeforeAll
    static void setUpBeforeAll() throws IOException {
        Properties properties = new Properties();

        // `.env` 파일 로드
        properties.load(new FileInputStream(".env"));

        // AWS 정보 조회 (한 번만 하면 됨)
        accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        region = properties.getProperty("AWS_S3_REGION");
        bucket = properties.getProperty("AWS_S3_BUCKET");

        // 자격 증명 객체
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        // S3Client
        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        // S3Presigner
        s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }

    @AfterAll
    static void tearDownAfterAll() {
        s3Client.close();
        s3Presigner.close();
    }

    @Test
    @DisplayName("S3에 파일 업로드 테스트")
    void upload() {
        String key = resolveKey("upload");
        byte[] content = "Amazon S3 File Upload Test".getBytes(StandardCharsets.UTF_8);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("text/plain")
                    .build();

            // then(검증)
            assertDoesNotThrow(() ->
                    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(content)));

        } catch (Exception e) {
            throw new RuntimeException("S3 업로드 실패", e);
        } finally {
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(key));
        }
    }

    @Test
    @DisplayName("S3에서 파일 다운로드 테스트")
    void download_file() {
        // S3에 파일 업로드
        String key = resolveKey("download");
        byte[] content = "Amazon S3 File Download Test".getBytes(StandardCharsets.UTF_8);
        uploadFile(key, content);

        try {
            // 파일 다운로드
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            byte[] result = s3Client.getObjectAsBytes(getObjectRequest).asByteArray();

            // then(검증)
            assertArrayEquals(content, result);

        } catch (Exception e) {
            throw new RuntimeException("파일 다운로드 실패", e);
        } finally {
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(key));
        }
    }

    @Test
    @DisplayName("Presigned Url 생성 테스트")
    void create_presign_url() {
        // S3에 파일 업로드
        String key = resolveKey("PresignUrl");
        byte[] content = "Create Presign Url Test".getBytes(StandardCharsets.UTF_8);
        uploadFile(key, content);

        try {
            // 파일 다운로드
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .build();

            // Presign Url 생성
            GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                    .getObjectRequest(getObjectRequest)
                    .signatureDuration(Duration.ofMinutes(5))
                    .build();

            String signedUrl = s3Presigner.presignGetObject(getObjectPresignRequest).url().toString();

            // then(검증)
            assertNotNull(signedUrl);
            assertTrue(signedUrl.startsWith("https://"));
            assertTrue(signedUrl.contains("PresignUrl"));

        } catch (Exception e) {
            throw new RuntimeException("Presigned Url 생성 실패", e);
        } finally {
            s3Client.deleteObject(builder -> builder.bucket(bucket).key(key));
        }
    }

    // 다운로드와 PresignedUrl 생성 테스트를 위한 파일 업로드
    private void uploadFile(String key, byte[] content) {
        try {
            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType("text/plain")
                    .build();

            s3Client.putObject(putReq, RequestBody.fromBytes(content));
        } catch (Exception e) {
            throw new RuntimeException("S3 업로드 실패", e);
        }
    }

    private String resolveKey(String method) {
        return "test/" + method + "/" + UUID.randomUUID().toString() + ".txt";
    }
}
