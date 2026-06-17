package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path path) {
    this.root = path;
  }

  @PostConstruct
  void init() throws IOException {
    Files.createDirectories(root);
  }

  @Override
  public UUID put(UUID binaryContentId, byte[] bytes) throws IOException {
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while simulating delay", e);
    }
    Files.write(resolvePath(binaryContentId), bytes);
    return binaryContentId;
  }

  @Override
  public InputStream get(UUID binaryContentId) throws IOException {
    byte[] data = Files.readAllBytes(resolvePath(binaryContentId));
    return new ByteArrayInputStream(data);
  }

  @Override
  public void delete(UUID binaryContentId) throws IOException {
    Files.delete(resolvePath(binaryContentId));
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) throws IOException {
    InputStreamResource resource = new InputStreamResource(get(dto.id()));

    String encodedFileName = UriUtils.encode(dto.fileName(), StandardCharsets.UTF_8);

    return ResponseEntity.ok()
        .contentType(MediaType.APPLICATION_OCTET_STREAM)
        .header(HttpHeaders.CONTENT_DISPOSITION,
            // UTF-8 형태의 첨부파일 설정
            String.format("attachment; filename*=UTF-8''%s", encodedFileName))
        .body(resource);
  }

  Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}
