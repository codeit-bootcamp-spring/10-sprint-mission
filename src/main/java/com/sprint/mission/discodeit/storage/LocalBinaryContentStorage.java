package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import com.sprint.mission.discodeit.exception.binarycontent.BinaryContentNotFoundException;
import com.sprint.mission.discodeit.exception.binarycontent.FileIOException;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "discodeit.storage.type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path root;

  public LocalBinaryContentStorage(
      @Value("${discodeit.storage.local.root-path}") String rootPath
  ) {
    this.root = Path.of(rootPath);
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
  public UUID put(UUID id, byte[] data) {
    try {
      Files.write(resolvePath(id), data);
      return id;
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
}