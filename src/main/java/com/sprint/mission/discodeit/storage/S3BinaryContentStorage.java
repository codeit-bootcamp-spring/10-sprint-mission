package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.config.S3Properties;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.StorageDownloadFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.StorageGetFailedException;
import com.sprint.mission.discodeit.exception.binarycontent.StorageUploadFailedException;
import java.io.InputStream;
import java.net.URI;
import java.time.Duration;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "s3")
@RequiredArgsConstructor
public class S3BinaryContentStorage implements BinaryContentStorage {

  private final S3Client s3Client;
  private final S3Presigner s3Presigner;
  private final S3Properties s3Properties;
  private static final String FILE_PREFIX = "uploads/";

  @Override
  public UUID put(UUID id, byte[] bytes) {
    try {
      log.debug("S3 파일 업로드 시도: fileId={}", id);
      String key = generateKey(id);
      log.debug("S3 파일 업로드 키 변환 성공: fileId={}", id);
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(s3Properties.getBucket())
          .key(key)
          .build();
      log.debug("S3 파일 업로드 Request 생성 성공: fileId={}", id);
      s3Client.putObject(putObjectRequest, RequestBody.fromBytes(bytes));
      log.info("S3 파일 업로드 성공: fileId={}", id);
      return id;
    } catch (S3Exception e) {
      throw new StorageUploadFailedException()
          .addDetail("fileId", id)
          .addDetail("errorCode", e.awsErrorDetails().errorCode())
          .addDetail("errorMessage", e.awsErrorDetails().errorMessage());
    } catch (Exception e) {
      throw new StorageUploadFailedException()
          .addDetail("fileId", id)
          .addDetail("errorMessage", e.getMessage());
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      log.debug("S3 파일 가져오기 시도: fileId={}", id);
      String key = generateKey(id);
      log.debug("S3 파일 가져오기 키 변환 성공: fileId={}", id);
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(s3Properties.getBucket())
          .key(key)
          .build();
      log.debug("S3 파일 가져오기 Request 생성 성공: fileId={}", id);
      ResponseBytes<GetObjectResponse> objectBytes = s3Client.getObjectAsBytes(getObjectRequest);
      log.info("S3 파일 가져오기 성공: fileId={}", id);
      return objectBytes.asInputStream();
    } catch (S3Exception e) {
      throw new StorageGetFailedException()
          .addDetail("fileId", id)
          .addDetail("errorCode", e.awsErrorDetails().errorCode())
          .addDetail("errorMessage", e.awsErrorDetails().errorMessage());
    } catch (Exception e) {
      throw new StorageGetFailedException()
          .addDetail("fileId", id)
          .addDetail("errorMessage", e.getMessage());
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    log.debug("S3 파일 다운로드 시도: fileId={}", binaryContentDto.id());
    String key = generateKey(binaryContentDto.id());
    log.debug("S3 파일 다운로드 키 변환 성공: fileId={}", binaryContentDto.id());
    String presignedUrl = generatePresignedUrl(key, binaryContentDto.contentType());
    log.info("S3 파일 다운로드 성공: fileId={}", binaryContentDto.id());
    return ResponseEntity.status(HttpStatus.FOUND)
        .location(URI.create(presignedUrl))
        .build();
  }

  public String generatePresignedUrl(String key, String contentType) {
    try {
      log.debug("S3 URL 가져오기 시도: fileId={}", key);
      GetObjectRequest getObjectRequest = GetObjectRequest.builder()
          .bucket(s3Properties.getBucket())
          .key(key)
          .responseContentType(contentType)
          .build();
      log.debug("S3 URL 가져오기 객체 Request 생성 성공: fileId={}", key);
      GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
          .signatureDuration(Duration.ofMinutes(s3Properties.getPresignedUrlExpiration()))
          .getObjectRequest(getObjectRequest)
          .build();
      log.debug("S3 URL 가져오기 Presign Request 생성 성공: fileId={}", key);
      String url = s3Presigner.presignGetObject(presignRequest).url().toString();
      log.info("S3 URL 가져오기 성공: fileId={}", key);
      return url;
    } catch (S3Exception e) {
      throw new StorageDownloadFailedException()
          .addDetail("fileId", key)
          .addDetail("errorCode", e.awsErrorDetails().errorCode())
          .addDetail("errorMessage", e.awsErrorDetails().errorMessage());
    } catch (Exception e) {
      throw new StorageDownloadFailedException()
          .addDetail("fileId", key)
          .addDetail("errorMessage", e.getMessage());
    }
  }

  private String generateKey(UUID id) {
    return FILE_PREFIX + id.toString();
  }
}
