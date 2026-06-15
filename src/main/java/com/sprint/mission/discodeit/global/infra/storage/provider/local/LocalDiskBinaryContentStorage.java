package com.sprint.mission.discodeit.global.infra.storage.provider.local;

import com.sprint.mission.discodeit.common.support.DomainServiceSupport;
import com.sprint.mission.discodeit.global.infra.storage.exception.local.FileErrorCode;
import com.sprint.mission.discodeit.global.infra.storage.exception.local.FileException;
import com.sprint.mission.discodeit.global.infra.storage.provider.AbstractBinaryContentStorage;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalDiskBinaryContentStorage extends AbstractBinaryContentStorage {

  private final Path root;

  public LocalDiskBinaryContentStorage(
      @Qualifier("fileUploadWorker") Executor fileUploadWorker,
      @Value("${discodeit.storage.local.internal-path}") String internalPath,
      @Value("${discodeit.storage.local.root-path}") Path rootPath) {
    super(fileUploadWorker, internalPath);
    this.root = rootPath;
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path path = resolvePath(id);
    DomainServiceSupport.requireOrThrow(path, Predicate.not(this::isPresent),
        value -> new FileException(FileErrorCode.FILE_ALREADY_EXISTS, value));
    try {
      Files.write(path, bytes);
      return id;
    } catch (IOException exception) {
      throw new FileException(FileErrorCode.FILE_WRITE_ERROR, exception);
    }
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new FileException(FileErrorCode.ROOT_DIRECTORY_CREATION_FAILED, root);
    }
  }

  @Override
  protected String resolveUrl(UUID id) {
    return String.join("/", internalPath, id.toString());
  }

  @Override
  protected void existsOrThrow(UUID id) {
    Path path = resolvePath(id);
    DomainServiceSupport.requireOrThrow(path, this::isPresent,
        value -> new FileException(FileErrorCode.FILE_NOT_FOUND, value));
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  private boolean isPresent(Path path) {
    return path.toFile().exists();
  }
}
