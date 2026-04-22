package com.sprint.mission.discodeit.storage.s3;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sprint.mission.discodeit.config.S3Config;
import com.sprint.mission.discodeit.config.StorageProperties;
import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class S3BinaryContentStorageTest {

  private S3BinaryContentStorage storage;
  private S3Client s3Client;
  private S3Presigner s3Presigner;
  private String bucketName;
  private UUID binaryContentId;

  @BeforeEach
  void setUp() throws IOException {
    Properties properties = loadProperties();

    StorageProperties storageProperties = new StorageProperties();
    storageProperties.setType(properties.getProperty("STORAGE_TYPE", "s3"));
    storageProperties.getS3().setAccessKey(properties.getProperty("AWS_S3_ACCESS_KEY"));
    storageProperties.getS3().setSecretKey(properties.getProperty("AWS_S3_SECRET_KEY"));
    storageProperties.getS3().setRegion(properties.getProperty("AWS_S3_REGION"));
    storageProperties.getS3().setBucket(properties.getProperty("AWS_S3_BUCKET"));
    storageProperties.getS3().setPresignedUrlExpiration(
        Long.parseLong(properties.getProperty("AWS_S3_PRESIGNED_URL_EXPIRATION", "600"))
    );

    bucketName = storageProperties.getS3().getBucket();
    binaryContentId = UUID.randomUUID();

    S3Config s3Config = new S3Config();
    s3Client = s3Config.s3Client(storageProperties);
    s3Presigner = s3Config.s3Presigner(storageProperties);
    storage = new S3BinaryContentStorage(s3Client, s3Presigner, storageProperties);
  }

  @AfterEach
  void tearDown() {
    if (s3Client != null && bucketName != null && binaryContentId != null) {
      try {
        s3Client.deleteObject(DeleteObjectRequest.builder()
            .bucket(bucketName)
            .key(binaryContentId.toString())
            .build());
      } catch (Exception ignored) {
      }
    }

    if (s3Client != null) {
      s3Client.close();
    }

    if (s3Presigner != null) {
      s3Presigner.close();
    }
  }

  @Test
  void putTest() {
    byte[] content = "put-test".getBytes();

    UUID savedId = storage.put(binaryContentId, content);

    assertEquals(binaryContentId, savedId);
  }

  @Test
  void getTest() throws IOException {
    byte[] content = "get-test".getBytes();
    storage.put(binaryContentId, content);

    try (InputStream inputStream = storage.get(binaryContentId)) {
      byte[] downloaded = inputStream.readAllBytes();
      assertArrayEquals(content, downloaded);
    }
  }

  @Test
  void downloadTest() {
    byte[] content = "download-test".getBytes();
    storage.put(binaryContentId, content);

    BinaryContentDto metaData = new BinaryContentDto(
        binaryContentId,
        "download-test.txt",
        (long) content.length,
        "text/plain"
    );

    ResponseEntity<Void> response = storage.download(metaData);

    assertEquals(HttpStatus.FOUND, response.getStatusCode());
    String location = response.getHeaders().getFirst(HttpHeaders.LOCATION);
    assertNotNull(location);
    assertTrue(location.contains(bucketName));
    assertTrue(location.contains(binaryContentId.toString()));
  }

  private Properties loadProperties() throws IOException {
    Properties properties = new Properties();

    try (InputStream inputStream = Files.newInputStream(Path.of(".env"))) {
      properties.load(inputStream);
    }

    return properties;
  }
}
