package com.sprint.mission.discodeit.global.infra.storage.provider.s3;

import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.infra.storage.config.AwsProperties;
import com.sprint.mission.discodeit.global.infra.storage.exception.s3.AwsErrorCode;
import com.sprint.mission.discodeit.global.infra.storage.exception.s3.AwsException;
import com.sprint.mission.discodeit.global.infra.storage.provider.AbstractBinaryContentStorage;
import java.net.URL;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import lombok.experimental.Delegate;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
public class S3BinaryContentStorage extends AbstractBinaryContentStorage {

  private final Duration s3ActiveDuration;

  @Delegate
  private final AwsProperties properties;
  private final S3Client s3Client;
  private final S3Presigner s3Presigner;

  public S3BinaryContentStorage(
      @Qualifier("fileUploadWorker") Executor fileUploadWorker,
      @Value("${app.storage.s3.internal-path}") String internalPath,
      AwsProperties properties,
      S3Client s3Client,
      S3Presigner s3Presigner) {
    super(fileUploadWorker, internalPath);
    this.properties = properties;
    this.s3Client = s3Client;
    this.s3Presigner = s3Presigner;
    this.s3ActiveDuration = Duration.ofSeconds(presignedURLExpiration());
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    DomainServiceSupport.requireOrThrow(id, Predicate.not(this::isPresent),
        value -> new AwsException(AwsErrorCode.FILE_ALREADY_EXIST, value));
    try {
      s3Client.putObject(buildPutObjectRequest(id), RequestBody.fromBytes(bytes));
      return id;
    } catch (S3Exception e) {
      throw new AwsException(AwsErrorCode.S3_CONNECTION_FAILED, e);
    }
  }

  @Override
  protected String resolveUrl(UUID id) {
    URL presignedUrl = generatePresignedUrl(id);
    return Strings.concat(internalPath, presignedUrl.getFile());
  }

  @Override
  protected void existsOrThrow(UUID id) {
    DomainServiceSupport.requireOrThrow(id, this::isPresent,
        value -> new AwsException(AwsErrorCode.FILE_NOT_FOUND, value));
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
