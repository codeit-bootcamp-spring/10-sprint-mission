package com.sprint.mission.discodeit.storage.s3;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Disabled("AWS세팅 지운 상태라 테스트 불가")
class S3BinaryContentStorageTest {

  private S3BinaryContentStorage storage;
  private S3Client s3Client;
  private String bucket;

  @BeforeEach
  void setUp() throws Exception {
    Properties prop = new Properties();
    try (FileInputStream fis = new FileInputStream(".env")) {
      prop.load(fis);
    }

    String accessKey = prop.getProperty("AWS_S3_ACCESS_KEY");
    String secretKey = prop.getProperty("AWS_S3_SECRET_KEY");
    String region = prop.getProperty("AWS_S3_REGION");
    bucket = prop.getProperty("AWS_S3_BUCKET");
    long presignedUrlExpiration =
        Long.parseLong(prop.getProperty("AWS_S3_PRESIGNED_URL_EXPIRATION", "600"));

    AwsBasicCredentials credentials =
        AwsBasicCredentials.create(accessKey, secretKey);

    s3Client = S3Client.builder()
        .region(Region.of(region))
        .credentialsProvider(StaticCredentialsProvider.create(credentials))
        .build();

    storage = new S3BinaryContentStorage(
        accessKey,
        secretKey,
        region,
        bucket,
        presignedUrlExpiration
    );
  }

  @Test
  @DisplayName("S3에 파일을 정상 업로드할 수 있다.")
  void put_success() {
    // given
    UUID id = UUID.randomUUID();
    String key = id.toString();
    byte[] data = "hello s3".getBytes(UTF_8);

    // when
    UUID result = storage.put(id, data);

    // then
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    ResponseBytes<GetObjectResponse> response = s3Client.getObjectAsBytes(getRequest);

    assertThat(result).isEqualTo(id);
    assertThat(response.asByteArray()).isEqualTo(data);

    deleteIfExists(key);
  }

  @Test
  @DisplayName("S3에 저장된 파일을 조회 할 수 있다.")
  void get_success() throws IOException {
    // given
    UUID id = UUID.randomUUID();
    String key = id.toString();
    byte[] data = "hello s3".getBytes(UTF_8);

    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.putObject(putRequest, RequestBody.fromBytes(data));

    // when
    byte[] result;
    try (InputStream inputStream = storage.get(id)) {
      result = inputStream.readAllBytes();

      // then
      assertThat(result).isEqualTo(data);

      deleteIfExists(key);
    }
  }

  @Test
  @DisplayName("다운로드 요청 시 URL로 응답을 반환 한다.")
  void download_success() {
    UUID id = UUID.randomUUID();
    String key = id.toString();
    byte[] data = "hello s3".getBytes(UTF_8);

    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.putObject(putRequest, RequestBody.fromBytes(data));

    BinaryContentDto binaryContentDto = mock(BinaryContentDto.class);
    given(binaryContentDto.id()).willReturn(id);

    // when
    ResponseEntity<?> response = storage.download(binaryContentDto);

    // then
    assertThat(response.getStatusCode().value()).isEqualTo(302);
    assertThat(response.getHeaders().getFirst("Location")).isNotBlank();
    assertThat(response.getHeaders().getFirst("Location")).contains(bucket);
    assertThat(response.getHeaders().getFirst("Location")).contains(key);

    deleteIfExists(key);

  }

  @Test
  @DisplayName("S3에 저장된 파일을 정상 삭제할 수 있다.")
  void delete_success() {
    // given
    UUID id = UUID.randomUUID();
    String key = id.toString();
    byte[] data = "hello s3".getBytes(UTF_8);

    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.putObject(putRequest, RequestBody.fromBytes(data));

    // when
    storage.delete(id);

    // then
    GetObjectRequest getRequest = GetObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    assertThrows(NoSuchKeyException.class, () -> s3Client.getObjectAsBytes(getRequest));
  }

  @Test
  @DisplayName("Presigned URL을 정상 생성할 수 있다.")
  void generate_presigned_url_success() {
    // given
    UUID id = UUID.randomUUID();
    String key = id.toString();
    byte[] data = "hello s3".getBytes(UTF_8);

    PutObjectRequest putRequest = PutObjectRequest.builder()
        .bucket(bucket)
        .key(key)
        .build();

    s3Client.putObject(putRequest, RequestBody.fromBytes(data));

    // when
    String presignedUrl = storage.generatePresignedUrl(key);

    // then
    assertThat(presignedUrl).isNotBlank();
    assertThat(presignedUrl).contains(bucket);
    assertThat(presignedUrl).contains(key);

    deleteIfExists(key);
  }

  private void deleteIfExists(String key) {
    try {
      s3Client.deleteObject(DeleteObjectRequest.builder()
          .bucket(bucket)
          .key(key)
          .build());
    } catch (Exception ignored) {
    }
  }
}
