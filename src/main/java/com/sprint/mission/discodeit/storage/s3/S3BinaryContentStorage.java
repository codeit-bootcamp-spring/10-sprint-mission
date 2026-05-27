package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.response.BinaryContentDTO;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
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
import java.time.Duration;
import java.util.UUID;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {
    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final String bucketName;
    private final long presignedUrlExpiration;

    public S3BinaryContentStorage(
            @Value("${discodeit.storage.s3.access-key}") String accessKey,
            @Value("${discodeit.storage.s3.secret-key}") String secretKey,
            @Value("${discodeit.storage.s3.region}") String region,
            @Value("${discodeit.storage.s3.bucket}") String bucket,
            @Value("${discodeit.storage.s3.presigned-url-expiration}") long presignedUrlExpiration
    ) {
        // 임시 credentials
        StaticCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );
        // region, client, presigner, bucket, presignedUrlExpiration
        Region awsRegion = Region.of(region);
        s3Client = S3Client.builder()
                .region(awsRegion)
                .credentialsProvider(credentialsProvider)
                .build();
        s3Presigner = S3Presigner.builder()
                .region(awsRegion)
                .credentialsProvider(credentialsProvider)
                .build();
        bucketName = bucket;
        this.presignedUrlExpiration = presignedUrlExpiration;
    }
    @Override
    public UUID put(UUID contentID, byte[] bytes) {
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(contentID.toString())
                        .build(),
                RequestBody.fromBytes(bytes)
        );
        return contentID;
    }

    @Override
    public InputStream get(UUID contentID) {
        return s3Client.getObject(
                GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(contentID.toString())
                        .build()
        );
    }

    @Override
    public ResponseEntity<?> download(BinaryContentDTO content) {
        String presignedUrl = s3Presigner.presignGetObject(
                GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofMinutes(10))
                        .getObjectRequest(r -> r
                                .bucket(bucketName)
                                .key(content.id().toString())
                        ).build()
        ).url().toString();
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, presignedUrl)
                .build();
    }
}
