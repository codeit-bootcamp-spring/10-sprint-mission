package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") String rootPath) {
    this.root = Paths.get(rootPath);
  }

  @PostConstruct
  public void init() {
    try {
      if (!Files.exists(root)) {
        Files.createDirectories(root);
        log.info("로컬 저장소 디렉토리를 초기화했습니다: {}", root.toAbsolutePath());
      }
    } catch (IOException e) {
      throw new RuntimeException("로컬 저장소 초기화 실패", e);
    }
  }

  @Override
  public UUID put(UUID id, byte[] data) {
    Path targetPath = resolvePath(id);
    try {
      Files.write(targetPath, data);
      return id;
    } catch (IOException e) {
      throw new RuntimeException("파일 저장 실패", e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    Path targetPath = resolvePath(id);
    try {
      return Files.newInputStream(targetPath);
    } catch (IOException e) {
      throw new RuntimeException("파일 읽기 실패", e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    InputStream inputStream = get(dto.id());

    Resource resource = new InputStreamResource(inputStream);

    // 다운로드 시 파일명이 깨지는 것을 방지하기 위한 헤더
    String contentDisposition = ContentDisposition.builder("attachment")
        .filename(dto.fileName(), StandardCharsets.UTF_8)
        .build()
        .toString();

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
        .contentType(MediaType.parseMediaType(dto.contentType()))
        .contentLength(dto.size()) // DTO의 사이즈 정보를 활용
        .body(resource);
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }
}
