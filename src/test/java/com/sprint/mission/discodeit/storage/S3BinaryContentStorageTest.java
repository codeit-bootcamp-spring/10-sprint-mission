package com.sprint.mission.discodeit.storage;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@SpringBootTest(properties = "discodeit.storage.type=s3")
@EnabledIfEnvironmentVariable(named = "AWS_S3_ACCESS_KEY", matches = ".*")
class S3BinaryContentStorageTest {

  @Autowired
  private BinaryContentStorage storage;

  @Test
  @DisplayName("type이 s3일 때 S3BinaryContentStorage가 주입되어야 한다.")
  void shouldInjectS3StorageWhenTypeIsS3() {
    // then
    assertThat(storage).isInstanceOf(S3BinaryContentStorage.class);
  }

  @Test
  @DisplayName("S3에 파일을 업로드하고 다시 다운로드(get)할 수 있다.")
  void putAndGetTest() throws Exception {
    // given
    UUID fileId = UUID.randomUUID();
    byte[] testData = "Discodeit S3 Integration Test".getBytes();

    // when: 업로드
    UUID savedId = storage.put(fileId, testData);

    // then: S3에서 스트림으로 가져와서 내용 검증
    try (InputStream inputStream = storage.get(savedId)) {
      String result = new String(inputStream.readAllBytes());
      assertThat(savedId).isEqualTo(fileId);
      assertThat(result).isEqualTo("Discodeit S3 Integration Test");
    }
  }

  @Test
  @DisplayName("download 호출 시 S3 Presigned URL과 함께 302 Redirect 응답을 반환해야 한다.")
  void downloadRedirectTest() {
    // given
    BinaryContentDto dto = new BinaryContentDto(
        UUID.randomUUID(),
        "test-image.png",
        1024L,
        "image/png"
    );

    // when
    ResponseEntity<?> response = storage.download(dto);

    // then
    // 1. 상태 코드가 302 FOUND 인지 확인
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

    // 2. 바디가 비어있는지(Void) 확인
    assertThat(response.getBody()).isNull();

    // 3. 응답 헤더 Location 에 생성된 Presigned URL이 있는지 확인
    assertThat(response.getHeaders().getLocation()).isNotNull();
    assertThat(response.getHeaders().getLocation().toString()).contains("amazonaws.com");
  }
}