package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;

  @Value("${discodeit.storage.s3.presigned-url-expiration:600}")
  long presignedUrlExpiration;

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
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    getS3Client().putObject(
        PutObjectRequest.builder()
            .bucket(bucket)
            .key(id.toString())
            .build(),
        RequestBody.fromBytes(bytes)
    );
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    return getS3Client().getObject(
        GetObjectRequest.builder()
            .bucket(bucket)
            .key(id.toString())
            .build()
    );
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    String url = generatePresignedUrl(
        binaryContentDto.id().toString(),
        binaryContentDto.contentType()
    );
    return ResponseEntity.status(302)
        .location(URI.create(url))
        .build();
  }

  private S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(
            AwsBasicCredentials.create(accessKey, secretKey)
        ))
        .build();

    return presigner.presignGetObject(
        GetObjectPresignRequest.builder()
            .getObjectRequest(r -> r.bucket(bucket).key(key))
            .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
            .build()
    ).url().toString();

  }
}
