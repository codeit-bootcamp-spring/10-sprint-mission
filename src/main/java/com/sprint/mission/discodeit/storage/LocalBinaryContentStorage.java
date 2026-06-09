package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.event.ErrorNotificationEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriUtils;

@Slf4j
@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;
  private final ApplicationEventPublisher applicationEventPublisher;

  LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path root,
      ApplicationEventPublisher applicationEventPublisher) {
    this.root = root;
    this.applicationEventPublisher = applicationEventPublisher;
    init();
  }

  private void init() {
    if (Files.notExists(root)) {
      try {
        Files.createDirectories(root);//디렉토리 없으면 생성
      } catch (IOException e) {
        throw new RuntimeException("바이너리 저장소 생성 실패", e);
      }
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @Retryable(
      retryFor = {
          RuntimeException.class
      },
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000)
  )
  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path path = resolvePath(id);
    try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
      fos.write(bytes);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    log.info("바이너리 컨텐츠 저장 성공: binaryContentId={}", id);
    return id;
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    if (!Files.exists(path)) {
      throw new RuntimeException("파일을 찾을 수 없음" + id);
    }
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    log.debug("바이너리 컨텐츠 다운로드 시도: binaryContentId={}", binaryContentDto.id());
    InputStream is = get(binaryContentDto.id());
    Resource resource = new InputStreamResource(is);
    String encodeFile = UriUtils.encode(binaryContentDto.fileName(), StandardCharsets.UTF_8);
    log.info("바이너리 컨텐츠 다운로드 성공: binaryContentId={}", binaryContentDto.id());
    return ResponseEntity.ok()
        .header("Content-Disposition", "attachment; filename=\"" + encodeFile + "\"")
        .header("Content-Type", binaryContentDto.contentType())
        .contentLength(binaryContentDto.size())
        .body(resource);
  }

  @Recover
  public UUID recover(RuntimeException e, UUID id, byte[] bytes) {
    // MDC에서 키 꺼내기
    String requestId = MDC.get("request_id");
    String content =
        "RequestId: " + requestId + "\nContentId: " + id + "\nError: " + e.getMessage();
    applicationEventPublisher.publishEvent(new ErrorNotificationEvent(
        "로컬 파일 업로드 실패",
        content
    ));
    throw e;
  }
}
