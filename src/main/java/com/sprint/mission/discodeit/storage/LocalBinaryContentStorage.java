package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserRole;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileIOException;
import com.sprint.mission.discodeit.repository.UserRepository;
import com.sprint.mission.discodeit.service.NotificationService;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;
  private final NotificationService notificationService;
  private final UserRepository userRepository;


  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") String rootPath,
      NotificationService notificationService,
      UserRepository userRepository

  ) {
    this.root = Path.of(rootPath);
    this.notificationService = notificationService;
    this.userRepository = userRepository;
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new FileIOException();
    }
  }

  @Override
  @Retryable(
      retryFor = FileIOException.class,
      maxAttempts = 3,
      backoff = @Backoff(delay = 1000, multiplier = 2)
  )
  public UUID put(UUID id, byte[] data) {
    try {
      Thread.sleep(3000);
      Files.write(resolvePath(id), data);
      return id;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException("Thread interrupted while simulating delay", e);
    } catch (IOException e) {
      throw new FileIOException();
    }
  }

  @Override
  public InputStream get(UUID id) {
    try {
      Path path = resolvePath(id);
      if (!Files.exists(path)) {
        throw new BinaryContentNotFoundException();
      }
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new FileIOException();
    }
  }

  @Override
  public ResponseEntity<?> download(BinaryContentDto binaryContentDto) {
    Resource resource = new InputStreamResource(get(binaryContentDto.id()));

    MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
    if (binaryContentDto.contentType() != null && !binaryContentDto.contentType().isBlank()) {
      mediaType = MediaType.parseMediaType(binaryContentDto.contentType());
    }

    return ResponseEntity.ok()
        .contentType(mediaType)
        .contentLength(binaryContentDto.size())
        .header(
            HttpHeaders.CONTENT_DISPOSITION,
            ContentDisposition.attachment()
                .filename(binaryContentDto.fileName())
                .build()
                .toString()
        )
        .body(resource);
  }

  @Override
  public void delete(UUID id) {
    try {
      Files.deleteIfExists(resolvePath(id));
    } catch (IOException e) {
      throw new FileIOException();
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  @Recover
  public UUID recover(FileIOException e, UUID id, byte[] ignoredData) {
    // MDC에서 requestId꺼내기
    String requestId = MDC.get("requestId");
    if (requestId == null) {
      requestId = "UNKNOWN";
    }

    // title,content 만들기
    String title = "바이너리 콘텐츠 저장 실패";

    String content = """
        FailedTask: BinaryContentStorage.put
        RequestId: %s
        BinaryContentId: %s
        Error: %s
        """
        .formatted(
            requestId,
            id,
            e.getMessage()
        );

    // 관리자 목록 조회
    List<User> admins = userRepository.findAllByRole(UserRole.ADMIN);

    for (User admin : admins) {
      notificationService.create(
          admin.getId(),
          title,
          content
      );
    }

    // 예외 던지기
    throw e;
  }
}