package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.BinaryContentDto;
import com.sprint.mission.discodeit.exception.common.CommonErrorCode;
import com.sprint.mission.discodeit.exception.common.CommonException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

//@Component
//@EnableConfigurationProperties(LocalBCStorageProperties.class)
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(LocalBCStorageProperties properties) {
    this.root = properties.rootPath();
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path path = resolvePath(id);
    if (isPresent(path)) {
      throw new CommonException(CommonErrorCode.FILE_ALREADY_EXIST, Map.of("path", path));
    }
    try {
      Files.write(path, bytes);
      return id;
    } catch (IOException e) {
      throw new CommonException(CommonErrorCode.FILE_CANT_WRITE,
          Map.of("IOException", e.getMessage()));
    }
  }

  @Override
  public InputStream get(UUID id) {
    Path path = resolvePath(id);
    if (!isPresent(path)) {
      throw getIncorrectPathException(path);
    }
    try {
      return Files.newInputStream(path);
    } catch (IOException e) {
      throw new CommonException(CommonErrorCode.FILE_CANT_READ,
          Map.of("IOException", e.getMessage()));
    }
  }

  private CommonException getIncorrectPathException(Path path) {
    return new CommonException(CommonErrorCode.FILE_NOT_FOUND, Map.of("path", path));
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto dto) {
    try (InputStream inputStream = get(dto.id())) {
      return ResponseEntity.status(HttpStatus.OK)
          .contentLength(dto.size())
          .contentType(MediaType.IMAGE_JPEG)
          .body(new InputStreamResource(inputStream));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(root);
    } catch (IOException e) {
      throw new CommonException(CommonErrorCode.ROOT_DIRECTORY_FAILED_TO_CREATE,
          Map.of("root-path", root));
    }
  }

  private Path resolvePath(UUID id) {
    return root.resolve(id.toString());
  }

  private boolean isPresent(Path path) {
    return path.toFile().exists();
  }
}
