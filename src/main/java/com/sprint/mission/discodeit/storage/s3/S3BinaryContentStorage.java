package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;

public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;
    private final long presignedUrlExpiration;

    public S3BinaryContentStorage(
            String accessKey,
            String secretKey,
            String region,
            String bucket,
            long presignedUrlExpiration
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
    }

    public S3BinaryContentStorage(
            String accessKey,
            String secretKey,
            String region,
            String bucket
    ) {
        this(accessKey, secretKey, region, bucket, 600);
    }

    // 파일 업로드
    @Override
    public UUID put(UUID id, byte[] bytes) {

        S3Client s3Client = getS3Client();
        // s3Client 통해 업로드
        s3Client.putObject(
                requestBuilder -> requestBuilder
                        .bucket(bucket)
                        .key(id.toString()),
                RequestBody.fromBytes(bytes)
        );
        // 파일 다운로드에 쓸 key
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        S3Client s3Client = getS3Client();

        return s3Client.getObject(
                requestBuilder -> requestBuilder
                        .bucket(bucket)
                        .key(id.toString())
        );
    }
    // get과 차이점은 URL 받아 Client가 직접 파일 가져옴
    @Override
    public ResponseEntity<Void> download(BinaryContentDto binaryContentDto) {
        String presignedUrl = generatePresignedUrl(
                binaryContentDto.id().toString(),
                binaryContentDto.contentType()
        );
    // URL 반환
        return ResponseEntity
                .status(302)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }

    private S3Client getS3Client() {
        // AWS 기본 자격 증명 객체 생성
        AwsBasicCredentials awsBasicCredentials =
                AwsBasicCredentials.create(accessKey, secretKey);

        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(awsBasicCredentials)
                )
                .build();
    }

    private String generatePresignedUrl(String key, String contentType) {
        AwsBasicCredentials awsBasicCredentials =
                AwsBasicCredentials.create(accessKey, secretKey);

        try (S3Presigner presigner = S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(awsBasicCredentials)
                )
                .build()) {

            // 어떤 S3 객체에 대한 URL 만들 지 지정
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .responseContentType(contentType)
                    .build();

            // URL 유효시간 포함 요청
            GetObjectPresignRequest presignRequest =
                    GetObjectPresignRequest.builder()
                            .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                            .getObjectRequest(getObjectRequest)
                            .build();

            return presigner.presignGetObject(presignRequest)
                    .url()
                    .toString();
        }
    }
}