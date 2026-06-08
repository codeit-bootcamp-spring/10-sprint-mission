package com.sprint.mission.discodeit.global.infra.storage.local;

import com.sprint.mission.discodeit.binarycontent.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.common.exception.file.FileErrorCode;
import com.sprint.mission.discodeit.common.exception.file.FileException;
import com.sprint.mission.discodeit.global.infra.storage.BaseBinaryContentStorage;
import com.sprint.mission.discodeit.global.log.ServiceLogAround;
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
public class LocalDiskBinaryContentStorage extends BaseBinaryContentStorage {

  private final Path root;

  public LocalDiskBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") Path rootPath,
      @Qualifier("fileUploadWorker") Executor fileUploadWorker) {
    super(fileUploadWorker, "/internal-local");
    this.root = rootPath;
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path path = resolvePath(id);
    throwOrNot(path, Predicate.not(this::isPresent),
        value -> new FileException(FileErrorCode.FILE_ALREADY_EXISTS, value));
    try {
      Files.write(path, bytes);
      return id;
    } catch (IOException exception) {
      throw new FileException(FileErrorCode.FILE_WRITE_ERROR, exception);
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
      throw new FileException(FileErrorCode.FILE_READ_ERROR, path);
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
      throw new FileException(FileErrorCode.ROOT_DIRECTORY_CREATION_FAILED, root);
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  private boolean isPresent(Path path) {
    return path.toFile().exists();
  }
}
