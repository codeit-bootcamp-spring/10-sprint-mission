package com.sprint.mission.discodeit.storage.s3;

import static org.assertj.core.api.Assertions.assertThat;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class AWSS3Test {

  private S3BinaryContentStorage storage;
  private UUID testId;
  private byte[] testData;
  private String testContentType;

  @BeforeEach
  void setUp() throws Exception {
    Properties env = new Properties();
    try (InputStream is = new FileInputStream(".env")) {
      env.load(is);
    }

    storage = new S3BinaryContentStorage();
    storage.setAccessKey(env.getProperty("AWS_S3_ACCESS_KEY"));
    storage.setSecretKey(env.getProperty("AWS_S3_SECRET_KEY"));
    storage.setRegion(env.getProperty("AWS_S3_REGION"));
    storage.setBucket(env.getProperty("AWS_S3_BUCKET"));

    testId = UUID.randomUUID();
    testData = "달선테스트".getBytes();
    testContentType = "text/plain";
  }

  @Test
  @DisplayName("S3 업로드 테스트")
  void uploadTest() {
    // when
    UUID result = storage.put(testId, testData);

    // then
    assertThat(result).isEqualTo(testId);
  }

  @Test
  @DisplayName("S3 스트림 다운로드 테스트")
  void getStreamTest() throws Exception {
    // given
    storage.put(testId, testData);

    // when
    try (InputStream is = storage.get(testId)) {
      byte[] downloadedBytes = is.readAllBytes();

      // then
      assertThat(downloadedBytes).isEqualTo(testData);
    }
  }

  @Test
  @DisplayName("S3 다운로드 리다이렉트 응답 테스트")
  void downloadRedirectTest() {
    // given
    storage.put(testId, testData);

    BinaryContentDto dto = new BinaryContentDto(
        testId,
        "dalsun.txt",
        (long) testData.length,
        testContentType
    );

    // when
    ResponseEntity<?> response = storage.download(dto);

    // then
    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

    URI location = response.getHeaders().getLocation();
    assertThat(location).isNotNull();
    assertThat(location.toString()).contains(testId.toString());
    assertThat(location.toString()).contains("Content-Type=" + testContentType.replace("/", "%2F"));
    System.out.println("Redirect Location: " + location);
  }

  @Test
  @DisplayName("Presigned URL 생성 테스트")
  void generatePresignedUrlTest() {
    // given
    storage.put(testId, testData);

    // when
    String url = storage.generatePresignedUrl(testId.toString(), testContentType);

    // then
    assertThat(url).isNotBlank();
    assertThat(url).contains(storage.getBucket());
    assertThat(url).contains(testId.toString());
    System.out.println("Generated URL: " + url);
  }
}