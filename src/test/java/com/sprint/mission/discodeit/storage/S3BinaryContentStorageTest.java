package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class S3BinaryContentStorageTest {

  private S3BinaryContentStorage storage;

  @BeforeEach
  void setUp() {
    storage = new S3BinaryContentStorage(
        "dummy-access-key",
        "dummy-secret-key",
        "ap-northeast-2",
        "dummy-bucket"
    );
  }

  @Test
  void testGetS3Client() {
    S3Client s3Client = storage.getS3Client();
    assertNotNull(s3Client);
  }

  @Test
  void testGeneratePresignedUrl() {
    String objectKey = "test-object-key";
    String url = storage.generatePresignedUrl(objectKey);
    assertNotNull(url);
    assertTrue(url.contains(objectKey));
    assertTrue(url.contains("X-Amz-Signature"));
  }

  @Test
  void testDownload() {
    UUID id = UUID.randomUUID();
    BinaryContentDto dto = new BinaryContentDto(id, "test.txt", 100L, "text/plain", null);

    ResponseEntity<?> response = storage.download(dto);

    assertNotNull(response);
    assertTrue(response.getStatusCode().is3xxRedirection());
    assertNotNull(response.getHeaders().getLocation());
    assertTrue(response.getHeaders().getLocation().toString().contains(id.toString()));
  }
}
