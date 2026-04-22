package com.sprint.mission.discodeit.storage.local;

import com.sprint.mission.discodeit.common.logging.ServiceLogAround;
import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.file.FileErrorCode;
import com.sprint.mission.discodeit.exception.file.FileException;
import com.sprint.mission.discodeit.storage.BaseBinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage extends BaseBinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path rootPath,
      @Qualifier("fileUploadWorker") Executor fileUploadWorker) {
    super(fileUploadWorker, "/internal-local");
    this.root = rootPath;
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path path = resolvePath(id);
    throwOrNot(path, Predicate.not(this::isPresent),
        value -> new FileException(FileErrorCode.FILE_ALREADY_EXIST, value));
    try {
      Files.write(path, bytes);
      return id;
    } catch (IOException exception) {
      throw new FileException(FileErrorCode.FILE_CANT_WRITE, exception);
    }
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    throwOrNot(path, this::isPresent,
        value -> new FileException(FileErrorCode.FILE_NOT_FOUND, value));
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new FileException(FileErrorCode.FILE_CANT_READ, path);
    }
  }

  @Override
  @ServiceLogAround
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    return ResponseEntity.status(HttpStatus.OK)
        .header("X-Accel-Redirect",
            convertToInternalPath(dto.id().toString()))
        .build();
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new FileException(FileErrorCode.ROOT_DIRECTORY_FAILED_TO_CREATE, root);
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  private boolean isPresent(Path path) {
    return path.toFile().exists();
  }
}
