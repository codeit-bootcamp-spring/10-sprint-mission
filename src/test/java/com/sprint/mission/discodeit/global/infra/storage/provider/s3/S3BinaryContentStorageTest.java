package com.sprint.mission.discodeit.global.infra.storage.provider.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.sprint.mission.discodeit.global.infra.storage.config.AwsProperties;
import com.sprint.mission.discodeit.global.infra.storage.exception.s3.AwsException;
import java.net.URL;
import java.util.UUID;
import java.util.concurrent.Executor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

class S3BinaryContentStorageTest {

  @Mock
  private Executor fileUploadWorker;

  @Mock
  private AwsProperties properties;

  @Mock
  private S3Client s3Client;

  @Mock
  private S3Presigner s3Presigner;

  private S3BinaryContentStorage storage;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    given(properties.bucket()).willReturn("test-bucket");
    given(properties.presignedURLExpiration()).willReturn(3600L);
    storage = new S3BinaryContentStorage(fileUploadWorker, "/internal/s3", properties, s3Client, s3Presigner);
  }

  @Test
  @DisplayName("put - 이미 존재하는 파일이면 예외가 발생한다.")
  void put_alreadyExists() {
    UUID id = UUID.randomUUID();
    given(s3Client.headObject(any(HeadObjectRequest.class))).willReturn(HeadObjectResponse.builder().build());

    assertThatThrownBy(() -> storage.put(id, new byte[]{1, 2, 3}))
        .isInstanceOf(AwsException.class);
  }

  @Test
  @DisplayName("put - 존재하지 않는 파일이면 정상적으로 업로드하고 ID를 반환한다.")
  void put_success() {
    UUID id = UUID.randomUUID();
    given(s3Client.headObject(any(HeadObjectRequest.class))).willThrow(NoSuchKeyException.builder().build());
    given(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
        .willReturn(PutObjectResponse.builder().build());

    UUID result = storage.put(id, new byte[]{1, 2, 3});

    assertThat(result).isEqualTo(id);
    verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
  }

  @Test
  @DisplayName("downloadUrl - 파일이 존재하면 presigned URL 경로를 기반으로 internal path와 조합된 URL을 반환한다.")
  void downloadUrl_success() throws Exception {
    UUID id = UUID.randomUUID();
    given(s3Client.headObject(any(HeadObjectRequest.class))).willReturn(HeadObjectResponse.builder().build());

    PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
    given(presignedRequest.url()).willReturn(new URL("https://s3.aws.com/test-bucket/" + id.toString() + "?token=123"));
    given(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).willReturn(presignedRequest);

    String url = storage.downloadUrl(id);

    assertThat(url).isEqualTo("/internal/s3/test-bucket/" + id.toString() + "?token=123");
  }

  @Test
  @DisplayName("downloadUrl - 파일이 존재하지 않으면 예외가 발생한다.")
  void downloadUrl_notFound() {
    UUID id = UUID.randomUUID();
    given(s3Client.headObject(any(HeadObjectRequest.class))).willThrow(NoSuchKeyException.builder().build());

    assertThatThrownBy(() -> storage.downloadUrl(id))
        .isInstanceOf(AwsException.class);
  }
}
