package com.sprint.mission.discodeit.storage.s3;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;

public class AWSS3Test {
    private String accessKey;
    private String secretKey;
    private String region;
    private String bucket;

    private S3Client s3Client;

    public AWSS3Test() throws IOException{
        Properties properties = new Properties();
        properties.load(new FileInputStream(".env"));

        this.accessKey = properties.getProperty("AWS_ACCESS_KEY_ID");
        this.secretKey = properties.getProperty("AWS_SECRET_ACCESS_KEY");
        this.region = properties.getProperty("AWS_REGION");
        this.bucket = properties.getProperty("AWS_BUCKET_NAME");

        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                )
                .build();
    }

    public void  upload(String key, String filePath){
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.putObject(putObjectRequest, Paths.get(filePath));
    }

    public void download(String key, String downloadPath){
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();
        s3Client.getObject(getRequest, Paths.get(downloadPath));
    }

    // 엑세스키를 사용하여 허용된 시간동안 s3에 접근할 수 있게 해줄 url 반환
    public String generatePresignedUrl(String key, int expireSeconds) {
        // 해당 엑세스키를 이용하여 s3로의 통로를 연결해줄 객체
        S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)
                ))
                .build();

        // 어떤 파일을 가져올지 요청
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // 얼마나 허용할지 시간을 설정
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                // 유효기간
                .signatureDuration(Duration.ofSeconds(expireSeconds))
                .getObjectRequest(getObjectRequest)
                .build();

        // url 생성
        PresignedGetObjectRequest presignedRequest = presigner.presignGetObject(presignRequest);
        // 문자열로 반환
        return presignedRequest.url().toString();
    }

    // 테스트용 메인 함수
    public static void main(String[] args) throws IOException {
        AWSS3Test test = new AWSS3Test();

        // 실제 경로와 키는 상황에 맞게 수정하세요.
        String localPath = "./uploads/test.txt";
        String s3Key = "sample/test-upload.txt";
        String downloadPath = "./uploads/test-download.txt";

        // 업로드 테스트
        test.upload(s3Key, localPath);

        // 다운로드 테스트
        test.download(s3Key, downloadPath);

        // PresignedUrl 생성 테스트
        String url = test.generatePresignedUrl(s3Key, 600); // 10분
    }
}
