package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;

@Disabled
public class AWSS3Test {
    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String bucketName;
    private String test = "test/hello.txt";

    // .env 파일에서 AWS 정보 로드
    @BeforeEach
    void loadEnv() throws Exception {
        Properties props = new Properties();
        props.load(new FileInputStream(".env"));

        // AWS Config 로드
        String accessKey = props.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = props.getProperty("AWS_S3_SECRET_KEY");
        Region region = Region.of(props.getProperty("AWS_S3_REGION"));
        bucketName = props.getProperty("AWS_S3_BUCKET");

        // 액세스키 + 시크릿키로 자격 증명 생성
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        // Client
        s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
        // presigner
        s3Presigner = S3Presigner.builder()
                .region(region)
                .credentialsProvider(credentialsProvider)
                .build();
    }

    @Test
    @DisplayName("업로드 테스트")
    void uploadFile() {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(test)
                        .build(),
                RequestBody.fromString("Hello S3")
        );
        System.out.println("업로드 완료: " + test);
    }

    @Test
    @DisplayName("다운로드 테스트")
    void download() {
        var response = s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(test)
                        .build()
        );
        System.out.println("다운로드 내용: " + response.asUtf8String());
    }

    @Test
    @DisplayName("PresignUrl 생성 테스트")
    void generatePresignedUrl() {
        var presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(r -> r.bucket(bucketName).key(test))
                .build();

        var presignUrl = s3Presigner.presignGetObject(presignRequest).url();
        System.out.println("Presigned URL: " + presignUrl);
    }



}
