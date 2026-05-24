package com.sprint.mission.discodeit.global.infra.storage.s3;

import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.common.exception.aws.AwsErrorCode;
import com.sprint.mission.discodeit.common.exception.aws.AwsException;
import com.sprint.mission.discodeit.global.config.aws.AwsProperties;
import com.sprint.mission.discodeit.global.infra.storage.BaseBinaryContentStorage;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
import java.io.InputStream;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import lombok.experimental.Delegate;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "s3")
public class S3BinaryContentStorage extends BaseBinaryContentStorage {

  private final Duration s3ActiveDuration;

  @Delegate
  private final AwsProperties properties;
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  public S3BinaryContentStorage(
      @Qualifier("fileUploadWorker") Executor fileUploadWorker,
      AwsProperties properties,
      S3Client s3Client,
      S3Presigner s3Presigner) {
    super(fileUploadWorker, "/internal-s3");
    this.properties = properties;
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
    this.s3ActiveDuration = Duration.ofSeconds(presignedURLExpiration());
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    throwOrNot(id, Predicate.not(this::isPresent),
        value -> new AwsException(AwsErrorCode.FILE_ALREADY_EXIST, value));
    try {
      s3Client.putObject(buildPutObjectRequest(id), RequestBody.fromBytes(bytes));
      return id;
    } catch (S3Exception e) {
      throw new AwsException(AwsErrorCode.S3_CONNECTION_FAILED, e);
    }
  }

  // useless...
  @Override
  public InputStream get(UUID id) {
    return s3Client.getObject(buildGetObjectRequest(id), ResponseTransformer.toInputStream());
  }

  @Override
  @ServiceLogAround
  public ResponseEntity<?> download(BinaryContentDto dto) {
    URL presignedUrl = generatePresignedUrl(dto.id());
    return ResponseEntity.status(HttpStatus.OK)
        .header("X-Accel-Redirect", convertToInternalPath(presignedUrl.getFile()))
        .build();
  }

  @Override
  protected String convertToInternalPath(String path) {
    return Strings.concat(internalPath, path);
  }

  private boolean isPresent(UUID id) {
    try {
      s3Client.headObject(buildHeadObjectRequest(id));
      return true;
    } catch (NoSuchKeyException e) {
      return false;
    } catch (S3Exception e) {
      throw new AwsException(AwsErrorCode.S3_CONNECTION_FAILED, e);
    }
  }

  private String resolvePath(UUID id) {
    return "https://%s.s3.%s.amazonaws.com/%s"
        .formatted(bucket(), region(), id);
  }

  private GetObjectRequest buildGetObjectRequest(UUID id) {
    return GetObjectRequest.builder()
        .bucket(bucket())
        .key(id.toString())
        .build();
  }

  private PutObjectRequest buildPutObjectRequest(UUID id) {
    return PutObjectRequest.builder()
        .bucket(bucket())
        .key(id.toString())
        .contentType("image/jpeg")
        .build();
  }

  private HeadObjectRequest buildHeadObjectRequest(UUID id) {
    return HeadObjectRequest.builder()
        .bucket(bucket())
        .key(id.toString())
        .build();
  }

  private URL generatePresignedUrl(UUID id) {
    GetObjectRequest getObjectRequest = buildGetObjectRequest(id);
    GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
        .signatureDuration(s3ActiveDuration)
        .getObjectRequest(getObjectRequest)
        .build();
    return s3Presigner.presignGetObject(presignRequest).url();
  }
}
