package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileDownloadException;
import com.sprint.mission.discodeit.exception.binarycontent.FileUploadException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
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
      throw new IllegalStateException("로컬 저장소 초기화 실패", e); // 기동 시점 에러는 시스템 크래시를 유도하기 위해 언체크 예외로 던짐
    }
  }

  @Override
  public UUID put(UUID id, byte[] data) {
    Path targetPath = resolvePath(id);

    // 동기/비동기 처리 간 응답 속도의 차이를 확인하기 위해 의도적인 지연 추가 (테스트 용도)
//    try {
//      Thread.sleep(3000); // 3초
//    } catch (InterruptedException e) {
//      Thread.currentThread().interrupt();
//      throw new RuntimeException("Thread interrupted while simulating delay", e);
//    }

    try {
      Files.write(targetPath, data);
      return id;
    } catch (IOException e) {
      log.error("파일 저장 실패 - id: {}", id, e);
      throw new FileUploadException(Map.of("fileId", id), e);
    }
  }

  @Override
  public InputStream get(UUID id) {
    Path targetPath = resolvePath(id);
    if (!Files.exists(targetPath)) {
      log.warn("이미지 파일이 존재하지 않음 - id: {}", id);
      throw new BinaryContentNotFoundException(Map.of("fileId", id));
    }
    try {
      return Files.newInputStream(targetPath);
    } catch (IOException e) {
      log.error("파일 읽기 실패 - id: {}", id, e);
      throw new FileDownloadException(Map.of("fileId", id), e);
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
