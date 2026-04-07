package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
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
@ConfigurationProperties(prefix = "discodeit.storage.s3")
@Slf4j
@Setter
@NoArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  private String accessKey;
  private String secretKey;
  private String region;
  private String bucket;

  private S3Client s3Client;
  private S3Presigner s3Presigner;

  //todo S3key는 현재 코드 구조 상 UUID를 사용

  @PostConstruct
  public void init() {
    log.info("S3 스토리지 활성화: 버킷명 = {}", bucket);
    this.s3Client = getS3Client();
    this.s3Presigner = getS3Presigner();
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try {
      String key = id.toString();
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build();
      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
      return id;
    } catch (Exception e) {
      throw new RuntimeException("S3 업로드 실패", e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    throw new UnsupportedOperationException("S3 스토리지에서는 InputStream 조회를 지원하지 않습니다.");
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto dto) {
    String signed = generatePresignedUrl(dto.id().toString(), dto.contentType(), dto.fileName());
    return ResponseEntity.status(302).location(URI.create(signed)).build();
  }

  private S3Client getS3Client() {
    if (accessKey != null && !accessKey.isBlank()) {
      return S3Client.builder()
          .region(Region.of(region))
          .credentialsProvider(StaticCredentialsProvider.create(
                  AwsBasicCredentials.create(
                      accessKey,
                      secretKey
                  )
              )
          ).build();
    }
    return S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  private S3Presigner getS3Presigner() {
    if (accessKey != null && !accessKey.isBlank()) {
      return S3Presigner.builder()
          .region(Region.of(region))
          .credentialsProvider(StaticCredentialsProvider.create(
                  AwsBasicCredentials.create(
                      accessKey,
                      secretKey
                  )
              )
          ).build();
    }
    return S3Presigner.builder()
        .region(Region.of(region))
        .credentialsProvider(DefaultCredentialsProvider.create())
        .build();
  }

  private String generatePresignedUrl(String key, String contentType, String fileName) {
    String name = (fileName != null && !fileName.isBlank())
        ? fileName
        : Paths.get(key).getFileName().toString();

    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType)
        .responseContentDisposition("attachment; filename=\"" + name + "\"")
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .getObjectRequest(getObjectRequest)
        .signatureDuration(Duration.ofMinutes(5))
        .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }
}
