package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.data.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(@Value("${storage.local.root}") String rootPath) {
    this.root = Path.of(rootPath);
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (Exception e) {
      throw new RuntimeException("저장소를 초기화할 수 없습니다", e);
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @Override
  public UUID put(UUID id, byte[] content) {
    try {
      Path file = resolvePath(id);
      Files.write(file, content);
      return id;
    } catch (Exception e) {
      throw new RuntimeException("파일을 저장할 수 없습니다: " + id, e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      Path file = resolvePath(id);
      if (!Files.exists(file)) {
        throw new IllegalArgumentException("파일이 존재하지 않습니다: " + id);
      }
      return Files.newInputStream(file);
    } catch (Exception e) {
      throw new RuntimeException("파일을 읽을 수 없습니다: " + id, e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    try {
      Path file = resolvePath(dto.id());
      Resource resource = new UrlResource(file.toUri());

      if (!resource.exists() || !resource.isReadable()) {
        throw new RuntimeException("파일을 읽을 수 없거나 존재하지 않습니다: " + dto.id());
      }

      String contentDisposition = "attachment; filename=\"" + dto.fileName() + "\"";

      return ResponseEntity.ok()
          .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
          .contentType(MediaType.parseMediaType(dto.contentType()))
          .contentLength(dto.size())
          .body(resource);
    } catch (MalformedURLException e) {
      throw new RuntimeException("다운로드 중 오류가 발생했습니다: " + e.getMessage());
    }
  }
}
