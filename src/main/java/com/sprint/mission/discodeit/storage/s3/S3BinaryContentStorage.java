package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Component
@Getter
@Setter
@ConfigurationProperties(prefix = "discodeit.storage.s3")
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try (S3Client s3Client = getS3Client()) {
      PutObjectRequest request = PutObjectRequest.builder()
          .bucket(bucket)
          .key(id.toString())
          .build();

      s3Client.putObject(request, RequestBody.fromBytes(bytes));
      return id;
    } catch (Exception e) {
      throw new RuntimeException("S3 업로드 중 오류 발생: " + e.getMessage());
    }
  }

  @Override
  public InputStream get(UUID uuid) {
    S3Client s3 = getS3Client();
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(uuid.toString())
        .build();
    return s3.getObject(request);
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    String key = binaryContentDto.id().toString();
    String contentType = binaryContentDto.contentType();
    String presignedUrl = generatePresignedUrl(key, contentType);

    return ResponseEntity
        .status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  public S3Client getS3Client() {
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        ).build();
  }

  public String generatePresignedUrl(String key, String contentType) {
    try (S3Presigner presigner = S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            )
        )
        .build()) {

      GetObjectRequest objectRequest = GetObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .responseContentType(contentType)
          .build();

      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(5))
          .getObjectRequest(objectRequest)
          .build();

      PresignedGetObjectRequest presigned = presigner.presignGetObject(presignRequest);
      return presigned.url().toString();
    }
  }
}
