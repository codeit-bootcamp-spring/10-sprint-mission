package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;


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

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String bucketName;
    private String objectKey;

    // 테스트 실행 전 .env의 AWS 설정값을 읽어 S3 클라이언트를 초기화 한다.
    @BeforeEach
    void setUp() throws IOException {
        Properties properties = loadProperties();

        String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        String region = properties.getProperty("AWS_S3_REGION");
        bucketName = properties.getProperty("AWS_S3_BUCKET");

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);

        s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .serviceConfiguration(S3Configuration.builder().build())
                .build();

        s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();

        objectKey = "test/" + UUID.randomUUID() + ".txt";
    }

    @AfterEach
    void tearDown() {
        if (s3Client != null && bucketName != null && objectKey != null) {
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build());
            } catch (Exception ignored) {
            }
        }

        if (s3Client != null) {
            s3Client.close();
        }

        if (s3Presigner != null) {
            s3Presigner.close();
        }
    }

    // 문자열 데이터를 S3에 업로드하는 동작이 정상 수행되는지 확인한다.
    @Test
    void uploadTest() {
        String content = "hello s3";

        // 업로드할 대상 버킷, 객체 키, 콘텐츠 타입을 지정한다.
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType("text/plain")
                .build();

        s3Client.putObject(request, RequestBody.fromString(content, StandardCharsets.UTF_8));

        assertNotNull(objectKey);
    }

    // S3에 업로드한 파일을 다시 다운로드했을 때 원본 내용과 일치하는지 확인한다.
    @Test
    void downloadTest() {
        String content = "download test";

        // 먼저 테스트용 데이터를 S3에 업로드한다.
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromString(content, StandardCharsets.UTF_8)
        );

        // 업로드한 객체를 바이트 형태로 다운로드한다.
        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .build()
        );

        // 다운로드한 바이트를 문자열로 변환한 뒤 원본과 비교한다.
        String downloaded = response.asString(StandardCharsets.UTF_8);
        assertEquals(content, downloaded);
    }

    // 특정 S3 객체에 접근할 수 있는 Presigned URL이 정상 생성되는지 확인한다.
    @Test
    void createPresignedUrlTest() {
        // Presigned URL을 만들 대상 파일을 먼저 S3에 업로드한다.
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectKey)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromString("presigned-url-test", StandardCharsets.UTF_8)
        );

        // URL 생성 대상이 되는 S3 객체 조회 요청을 만든다.
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        // 10분 동안 유효한 Presigned GET 요청을 생성하도록 설정한다.
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(getObjectRequest)
                .build();

        // 실제 Presigned URL을 생성한다.
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        URL url = presignedRequest.url();

        // 생성된 URL이 null이 아니고 비어 있지 않은지 확인한다.
        assertNotNull(url);
        assertFalse(url.toString().isBlank());

        // 생성 결과를 콘솔에서 확인할 수 있도록 출력한다.
        System.out.println("Presigned URL: " + url);
    }



    // 프로젝트 루트의 .env 파일에서 AWS 관련 설정값을 읽어온다.
    private Properties loadProperties() throws IOException {
        Properties properties = new Properties();

        try (InputStream inputStream = Files.newInputStream(Path.of(".env"))) {
            properties.load(inputStream);
        }

        return properties;
    }
}
