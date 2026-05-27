package com.sprint.mission.discodeit.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.io.InputStream;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

@SpringBootTest(properties = "discodeit.storage.type=s3")
@ActiveProfiles("test")
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

  @Nested
  @DisplayName("S3에 파일 업로드 테스트")
  class PutTest {

    @Test
    @DisplayName("성공: 정상적인 데이터가 주어지면 S3에 업로드하고 UUID를 반환한다")
    void put_and_get_Success() throws Exception {
      // given
      UUID fileId = UUID.randomUUID();
      byte[] testData = "Discodeit S3 Integration Test".getBytes();

      // when
      UUID savedId = storage.put(fileId, testData);

      // then
      assertThat(savedId).isEqualTo(fileId);

      // S3에 진짜 올라갔는지 get으로 확인
      try (InputStream inputStream = storage.get(savedId)) {
        String result = new String(inputStream.readAllBytes());
        assertThat(result).isEqualTo("Discodeit S3 Integration Test");
      }
    }

    @Test
    @DisplayName("실패: 데이터가 null로 주어지면 NullPointerException이 발생한다")
    void put_Fail_nullData() {
      // given
      UUID fileId = UUID.randomUUID();

      // when & then
      assertThatThrownBy(() -> storage.put(fileId, null))
          .isInstanceOf(
              NullPointerException.class); // RequestBody.fromBytes(null) 호출 시 AWS SDK 내부에서 NPE를 던짐
    }
  }


  @Nested
  @DisplayName("서버로 파일 다운로드 테스트")
  class GetTest {

    @Test
    @DisplayName("실패: S3에 존재하지 않는 ID로 조회하면 NoSuchKeyException이 발생한다.")
    void get_Fail_notFound() {
      // given
      UUID fakeId = UUID.randomUUID();

      // when & then
      // 가짜 ID로 조회를 시도하면 S3가 404를 반환하고, AWS SDK가 이를 NoSuchKeyException으로 변환하여 던짐
      assertThatThrownBy(() -> storage.get(fakeId))
          .isInstanceOf(NoSuchKeyException.class);
    }
  }

  @Nested
  @DisplayName("다운로드 링크 생성 테스트")
  class DownloadTest {

    @Test
    @DisplayName("성공: download 호출 시 S3 Presigned URL과 함께 302 Redirect 응답을 반환한다.")
    void downloadRedirect_Success() {
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
}