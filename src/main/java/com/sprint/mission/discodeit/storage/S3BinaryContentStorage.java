package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
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

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final String accessKey;
  private final String secretKey;
  private final String region;
  private final String bucket;

  private final S3Client s3Client;

  public S3BinaryContentStorage(
      @Value("${storage.s3.access-key}") String accessKey,
      @Value("${storage.s3.secret-key}") String secretKey,
      @Value("${storage.s3.region}") String region,
      @Value("${storage.s3.bucket}") String bucket) {
    this.accessKey = accessKey;
    this.secretKey = secretKey;
    this.region = region;
    this.bucket = bucket;

    this.s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build();
  }

  public S3Client getS3Client() {
    return s3Client;
  }

  @Override
  public UUID put(UUID id, byte[] content) {
    s3Client.putObject(
        PutObjectRequest.builder().bucket(bucket).key(id.toString()).build(),
        RequestBody.fromBytes(content)
    );
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    return s3Client.getObject(
        GetObjectRequest.builder().bucket(bucket).key(id.toString()).build()
    );
  }

  public String generatePresignedUrl(String objectKey) {
    try (S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .build()) {

      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(objectKey)
          .build();

      GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(10))
          .getObjectRequest(getObjectRequest)
          .build();

      return presigner.presignGetObject(getObjectPresignRequest).url().toString();
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    String url = generatePresignedUrl(binaryContentDto.id().toString());
    HttpHeaders headers = new HttpHeaders();
    headers.setLocation(URI.create(url));
    return new ResponseEntity<>(headers, HttpStatus.SEE_OTHER);
  }
}
