package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

class AWSS3Test {

    private static final String ENV_PATH = ".env";
    private static final String TEST_PREFIX = "tests/aws-s3/";

    private static AwsProperties awsProperties;
    private static S3Client s3Client;
    private static S3Presigner s3Presigner;

    private final List<String> createdKeys = new ArrayList<>();

    @BeforeAll
    static void setUp() throws IOException {
        Properties env = loadEnvProperties();

        awsProperties = new AwsProperties();
        awsProperties.setAccessKey(required(env, "AWS_S3_ACCESS_KEY"));
        awsProperties.setSecretKey(required(env, "AWS_S3_SECRET_KEY"));
        awsProperties.setRegion(required(env, "AWS_S3_REGION"));
        awsProperties.setBucket(required(env, "AWS_S3_BUCKET"));

        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                awsProperties.getAccessKey(),
                awsProperties.getSecretKey()
            )
        );

        s3Client = S3Client.builder()
            .region(Region.of(awsProperties.getRegion()))
            .credentialsProvider(credentialsProvider)
            .build();

        s3Presigner = S3Presigner.builder()
            .region(Region.of(awsProperties.getRegion()))
            .credentialsProvider(credentialsProvider)
            .build();
    }

    @AfterEach
    void tearDown() {
        for (String key : createdKeys) {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build());
        }
        createdKeys.clear();
    }

    @Test
    @DisplayName("업로드: 테스트 파일을 S3 버킷에 업로드한다")
    void upload() {
        String key = createTestKey("upload");
        String content = "upload-test-" + UUID.randomUUID();

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .contentType("text/plain")
                .build(),
            RequestBody.fromString(content, StandardCharsets.UTF_8)
        );

        createdKeys.add(key);

        Long contentLength = s3Client.headObject(HeadObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build())
            .contentLength();

        assertThat(contentLength).isEqualTo((long) content.getBytes(StandardCharsets.UTF_8).length);
    }

    @Test
    @DisplayName("다운로드: 업로드한 테스트 파일을 S3에서 내려받는다")
    void download() {
        String key = createTestKey("download");
        String expected = "download-test-" + UUID.randomUUID();
        uploadTextObject(key, expected);

        ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(
            GetObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .build()
        );

        assertThat(response.asUtf8String()).isEqualTo(expected);
    }

    @Test
    @DisplayName("PresignedUrl 생성: 서명된 다운로드 URL을 생성한다")
    void generatePresignedUrl() throws IOException {
        String key = createTestKey("presigned");
        String expected = "presigned-test-" + UUID.randomUUID();
        uploadTextObject(key, expected);

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(
            GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .getObjectRequest(GetObjectRequest.builder()
                    .bucket(awsProperties.getBucket())
                    .key(key)
                    .build())
                .build()
        );

        HttpURLConnection connection = (HttpURLConnection) new URL(
            presignedRequest.url().toString()).openConnection();
        connection.setRequestMethod("GET");

        try (InputStream inputStream = connection.getInputStream()) {
            String body = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            assertThat(connection.getResponseCode()).isEqualTo(HttpURLConnection.HTTP_OK);
            assertThat(presignedRequest.url().toString()).contains(awsProperties.getBucket());
            assertThat(body).isEqualTo(expected);
        } finally {
            connection.disconnect();
        }
    }

    private void uploadTextObject(String key, String content) {
        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(awsProperties.getBucket())
                .key(key)
                .contentType("text/plain")
                .build(),
            RequestBody.fromString(content, StandardCharsets.UTF_8)
        );
        createdKeys.add(key);
    }

    private String createTestKey(String scenario) {
        return TEST_PREFIX + scenario + "/" + UUID.randomUUID() + ".txt";
    }

    private static Properties loadEnvProperties() throws IOException {
        Path envPath = Path.of(ENV_PATH);
        assertThat(Files.exists(envPath))
            .as("%s 파일이 필요합니다.", ENV_PATH)
            .isTrue();

        Properties properties = new Properties();
        try (InputStream inputStream = Files.newInputStream(envPath)) {
            properties.load(inputStream);
        }
        return properties;
    }

    private static String required(Properties properties, String key) {
        String value = properties.getProperty(key);
        assertThat(value)
            .as("%s 값이 .env에 정의되어 있어야 합니다.", key)
            .isNotBlank();
        return value;
    }
}
