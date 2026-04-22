package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

    private final String accessKey;
    private final String secretKey;
    private final String region;
    private final String bucket;

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
    private long expiration;

    public S3BinaryContentStorage(
        @Value("${discodeit.storage.s3.access-key}") String accessKey,
        @Value("${discodeit.storage.s3.secret-key}") String secretKey,
        @Value("${discodeit.storage.s3.region}") String region,
        @Value("${discodeit.storage.s3.bucket}") String bucket
    ) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        this.bucket = bucket;

        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        Region awsRegion = Region.of(region);

        this.s3Client = S3Client.builder()
            .region(awsRegion)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();

        this.s3Presigner = S3Presigner.builder()
            .region(awsRegion)
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .build();
    }

    @Override
    public UUID put(UUID id, byte[] bytes) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(this.bucket)
            .key(id.toString())
            .build();

        getS3Client().putObject(putObjectRequest, RequestBody.fromBytes(bytes));
        return id;
    }

    @Override
    public InputStream get(UUID id) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(this.bucket)
            .key(id.toString())
            .build();

        return getS3Client().getObject(getObjectRequest);
    }

    @Override
    public ResponseEntity<Void> download(BinaryContentDto dto) {
        // Presigned URL 생성
        String presignedUrl = generatePresignedUrl(dto.id().toString(), dto.contentType());

        // 302 Redirect 로 응답 설정
        return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(presignedUrl))
            .build();
    }

    private S3Client getS3Client() {
        return this.s3Client;
    }

    private String generatePresignedUrl(String key, String contentType) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(this.bucket)
            .key(key)
            .responseContentType(contentType)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofSeconds(this.expiration))
            .getObjectRequest(getObjectRequest)
            .build();

        return this.s3Presigner.presignGetObject(presignRequest).url().toString();
    }
}