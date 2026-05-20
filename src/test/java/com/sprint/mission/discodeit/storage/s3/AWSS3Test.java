package com.sprint.mission.discodeit.storage.s3;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.nio.file.Paths;
import java.time.Duration;

@SpringBootTest
@Tag("integration")
public class AWSS3Test {

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Properties s3Properties;

    @Autowired
    private S3Presigner s3Presigner;

    @Test
    void upload_test() {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key("test-file.txt")
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString("Hello S3!"));
        System.out.println("업로드 완료!");
    }

    @Test
    void download_test() {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key("test-file.txt")
                .build();

        s3Client.getObject(getObjectRequest, Paths.get("downloaded-test.txt"));
        System.out.println("다운로드 완료!");
    }

    @Test
    void create_PresignedUrl_test() {
        // GetObjectRequest 객체 생성
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(s3Properties.getBucket())
                .key("test-file.txt")
                .build();

        // GetObjectPresignRequest 객체 생성
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofSeconds(600)) // 유효 시간?
                .getObjectRequest(getObjectRequest)
                .build();

        String url = s3Presigner.presignGetObject(presignRequest).url().toString();
        System.out.println("생성된 URL: " + url);
    }
}
