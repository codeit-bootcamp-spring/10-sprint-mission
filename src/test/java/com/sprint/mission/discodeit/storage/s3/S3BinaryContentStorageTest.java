package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import com.sprint.mission.discodeit.storage.BinaryContentStorage;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

// S3BinaryContentStorage는 Spring 의존성(JPA, DB 등) 없이 독립적으로 동작하므로 -> Spring Context 사용 X

// 환경변수가 존재하고 빈 값이 아니면 테스트 실행
@EnabledIfEnvironmentVariable(named = "AWS_ACCESS_KEY_ID", matches = ".+")
@DisplayName("S3BinaryContentStorage 테스트")
class S3BinaryContentStorageTest {

  private static BinaryContentStorage storage;

  private static final byte[] TEST_BYTES = "test_bytes".getBytes();
  private static final String TEST_FILE_NAME = "test_file.txt";
  private static final String TEST_CONTENT_TYPE = "text/plain";

  // S3 연결에 필요한 설정을 .env 파일에서 읽고, S3BinaryContentStorage를 초기화
  @BeforeAll
  static void setUp() throws IOException {
    Properties properties = new Properties();
    try (InputStream inputStream = new FileInputStream(Path.of(".env").toFile())) {
      properties.load(inputStream);
    }

    storage = new S3BinaryContentStorage(
        properties.getProperty("AWS_S3_ACCESS_KEY"),
        properties.getProperty("AWS_S3_SECRET_KEY"),
        properties.getProperty("AWS_S3_REGION"),
        properties.getProperty("AWS_S3_BUCKET"),
        Duration.ofMinutes(5)
    );
  }

  // put
  // S3에 파일을 업로드하고 동일한 ID를 반환하는지 검증
  @Test
  void should_return_same_id_when_put() {
    // given
    UUID id = UUID.randomUUID();

    // when
    UUID result = storage.put(id, TEST_BYTES);

    // then
    assertThat(result).isEqualTo(id);
  }

  // get
  // S3에 업로드한 파일을 다시 읽어서 내용이 동일한지 검증
  @Test
  void should_return_uploaded_content_when_get() throws IOException {
    // given
    UUID id = UUID.randomUUID();
    storage.put(id, TEST_BYTES);

    // when
    try (InputStream inputStream = storage.get(id)) {
      String content = new String(inputStream.readAllBytes());

      // then
      assertThat(content).isEqualTo(new String(TEST_BYTES));
    }
  }

  // 존재하지 않는 키로 get()을 호출하는 경우 예외가 발생하는지 확인
  @Test
  void should_throw_exception_when_get_nonexistent_key() {
    // given - 업로드한 적 없는 UUID
    UUID nonexistentId = UUID.randomUUID();

    // when & then - S3에서 NoSuchKeyException이 발생해야 함
    assertThatThrownBy(() -> storage.get(nonexistentId))
        .isInstanceOf(NoSuchKeyException.class);
  }

  // download
  // Presigned URL이 포함된 리다이렉트 응답을 검증
  @Test
  void should_redirect_with_presigned_url_when_download() {
    // given
    UUID id = UUID.randomUUID();
    storage.put(id, TEST_BYTES);
    BinaryContentDto metadata = new BinaryContentDto(
        id,
        TEST_FILE_NAME,
        (long) TEST_BYTES.length,
        TEST_CONTENT_TYPE
    );

    // when
    ResponseEntity<?> response = storage.download(metadata);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
    // Location 헤더에 해당 UUID가 포함된 Presigned URL이 있는지 검증
    String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
    assertThat(location).isNotNull();
    assertThat(location).contains(id.toString());
  }
}