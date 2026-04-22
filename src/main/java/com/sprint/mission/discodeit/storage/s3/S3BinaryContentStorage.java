package com.sprint.mission.discodeit.storage.s3;

import com.sprint.mission.discodeit.config.StorageProperties;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.InputStream;
import java.time.Duration;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final String bucket;
  private final long presignedUrlExpiration;

  public S3BinaryContentStorage(
      S3Client s3Client,
      S3Presigner s3Presigner,
      StorageProperties storageProperties
  ) {
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
    this.bucket = storageProperties.getS3().getBucket();
    this.presignedUrlExpiration = storageProperties.getS3().getPresignedUrlExpiration();
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) {
    PutObjectRequest request = PutObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .build();

    s3Client.putObject(request, RequestBody.fromBytes(bytes));
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) {
    GetObjectRequest request = GetObjectRequest.builder()
        .bucket(bucket)
        .key(binaryContentId.toString())
        .build();

    return s3Client.getObject(request);
  }

  @Override
  public ResponseEntity<Void> download(BinaryContentDto metaData) {
    String presignedUrl = generatePresignedUrl(metaData.id().toString(), metaData.contentType());

    return ResponseEntity.status(302)
        .header(HttpHeaders.LOCATION, presignedUrl)
        .build();
  }

  private String generatePresignedUrl(String key, String contentType) {
    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .responseContentType(contentType)
        .build();

    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
        .getObjectRequest(getObjectRequest)
        .build();

    return s3Presigner.presignGetObject(presignRequest).url().toString();
  }
}
