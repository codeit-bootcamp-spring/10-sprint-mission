package com.sprint.mission.discodeit.storage.s3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.Properties;
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

public class AWSS3Test {

    public static void main(String[] args) {
        Properties env = loadEnv(".env");

        String accessKey = env.getProperty("AWS_ACCESS_KEY");
        String secretKey = env.getProperty("AWS_SECRET_KEY");
        String region = env.getProperty("AWS_REGION");
        String bucket = env.getProperty("AWS_BUCKET");

        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                ).build();

        S3Presigner s3Presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKey, secretKey)
                        )
                ).build();

        // S3Client 테스트
        System.out.println("S3Client 생성 성공");
        System.out.println("bucket = " + bucket);

        // s3에 저장될 객체 이름
        String key = "test/hello.txt";
        String content = "hello s3";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("text/plain")
                .build();

        // 업로드 테스트
        s3Client.putObject(putObjectRequest, RequestBody.fromString(content));

        System.out.println("업로드 성공");
        System.out.println("업로드된 key = " + key);

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        // 다운로드 테스트
        try (ResponseInputStream<GetObjectResponse> inputStream =
                     s3Client.getObject(getObjectRequest);
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))){

            String line;
            StringBuilder result = new StringBuilder();

            while((line = reader.readLine()) != null) {
                result.append(line);
            }

            System.out.println("다운로드 성공");
            System.out.println("내용 = " + result);
        } catch (IOException e) {
            throw new RuntimeException("S3 파일 읽기 실패", e);
        }

        // presignedUrl 테스트
        GetObjectRequest presignTarget = GetObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .getObjectRequest(presignTarget)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest
                = s3Presigner.presignGetObject(presignRequest);

        String presignedUrl = presignedGetObjectRequest.url().toString();

        System.out.println("Presigned URL 생성 성공");
        System.out.println("URL = " + presignedUrl);
    }

    private static Properties loadEnv(String filePath) {
        Properties properties = new Properties();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                if (!line.contains("=")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                properties.setProperty(parts[0].trim(), parts[1].trim());
            }
        } catch (IOException e) {
            throw new RuntimeException(".env 파일을 읽는 중 오류가 발생했습니다.", e);
        }

        return properties;
    }


}