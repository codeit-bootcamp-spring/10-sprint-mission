package com.sprint.mission.discodeit.storage;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;

public class AWSS3Test {

    private static S3Client s3Client;
    private static S3Presigner s3Presigner;
    private static String bucketName;

    public static void main(String[] args) {
        try {
            // env 파일 로드 및 S3 클라이언트 초기화
            loadEnvAndInitS3();

            System.out.println("=== AWS S3 API 간단 테스트 시작 ===");

            // 테스트용 고유 파일명 생성
            String testObjectKey = "test-folder/sample-" + UUID.randomUUID() + ".txt";

            // 작업 별 테스트 메소드 순차적 실행
            testUpload(testObjectKey);
            testDownload(testObjectKey);
            testPresignedUrl(testObjectKey);

            System.out.println("=== AWS S3 API 테스트 정상 완료 ===");

        } catch (Exception e) {
            System.err.println("테스트 중 오류가 발생했습니다: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Properties 클래스를 활용하여 .env 파일 로드
     */
    private static void loadEnvAndInitS3() throws IOException {
        Properties properties = new Properties();
        // 프로젝트 최상위 디렉토리(user.dir)에 위치한 .env 파일을 읽어옴
        try (FileInputStream fis = new FileInputStream(Paths.get(System.getProperty("user.dir"), ".env").toFile())) {
            properties.load(fis);
        }

        String accessKey = properties.getProperty("AWS_S3_ACCESS_KEY");
        String secretKey = properties.getProperty("AWS_S3_SECRET_KEY");
        String regionString = properties.getProperty("AWS_S3_REGION");
        bucketName = properties.getProperty("AWS_S3_BUCKET");

        // AWS 자격 증명 및 리전 설정
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        Region region = Region.of(regionString);

        // API용 S3Client 및 URL 생성용 S3Presigner 객체 빌드
        s3Client = S3Client.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        s3Presigner = S3Presigner.builder()
            .region(region)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    /**
     * 업로드 테스트 메소드
     */
    public static void testUpload(String objectKey) {
        String testContent = "test file for S3";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromString(testContent));
        System.out.println("업로드 성공! 파일이 S3에 저장되었습니다. Key: " + objectKey);
    }

    /**
     * 다운로드 테스트 메소드
     */
    public static void testDownload(String objectKey) throws IOException {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build();

        try (ResponseInputStream<GetObjectResponse> s3Object = s3Client.getObject(getObjectRequest)) {
            String content = new String(s3Object.readAllBytes());
            System.out.println("S3에서 읽어온 내용: " + content);
        }
    }

    /**
     * PresignedUrl 생성 테스트 메소드
     */
    public static void testPresignedUrl(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(objectKey)
            .build();

        // 10분 동안만 유효한 다운로드 임시 링크 생성
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .getObjectRequest(getObjectRequest)
            .build();

        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        String url = presignedRequest.url().toString();

        System.out.println("Presigned URL 성공, 10분간 유효한 다운로드 링크 발급: " + url);
    }
}