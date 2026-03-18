package com.sprint.mission.discodeit.storage;

import com.sprint.mission.discodeit.dto.response.BinaryContentDto;
import com.sprint.mission.discodeit.exception.BusinessException;
import com.sprint.mission.discodeit.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(prefix = "discodeit.storage", name = "type", havingValue = "local")
public class LocalBinaryContentStorage implements BinaryContentStorage {

  private final Path rootPath;

  public LocalBinaryContentStorage(@Value("${discodeit.storage.local.root-path}") Path rootPath) {
    this.rootPath = rootPath.toAbsolutePath().normalize();
  }

  @PostConstruct
  public void init() {
    try {
      Files.createDirectories(rootPath);
    } catch (IOException e) {
      throw new BusinessException(ErrorCode.BINARY_CONTENT_STORAGE_INIT_FAILED);
    }
  }

  @Override
  public UUID put(UUID id, byte[] bytes) {
    Path target = resolvePath(id);
    try {
      Files.write(target, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
      return id;
    } catch (IOException e) {
      throw new BusinessException(ErrorCode.BINARY_CONTENT_SAVE_FAILED);
    }
  }

  @Override
  public InputStream get(UUID id) {
    Path target = resolvePath(id);
    if (!Files.exists(target)) {
      throw new BusinessException(ErrorCode.BINARY_CONTENT_NOT_FOUND);
    }

    try {
      return Files.newInputStream(target, StandardOpenOption.READ);
    } catch (IOException e) {
      throw new BusinessException(ErrorCode.BINARY_CONTENT_READ_FAILED);
    }
  }

  @Override
  public void delete(UUID id) {
    Path target = resolvePath(id);
    try {
      Files.deleteIfExists(target);
    } catch (IOException e) {
      throw new BusinessException(ErrorCode.BINARY_CONTENT_DELETE_FAILED);
    }
  }

  @Override
  public ResponseEntity<Resource> download(BinaryContentDto binaryContentDto) {
    InputStream inputStream = get(binaryContentDto.id());
    Resource resource = new InputStreamResource(inputStream);

    return ResponseEntity.ok()
        .header(HttpHeaders.CONTENT_DISPOSITION,
            buildContentDisposition(binaryContentDto).toString())
        .contentType(resolveMediaType(binaryContentDto.contentType()))
        .contentLength(binaryContentDto.size())
        .body(resource);
  }

  private Path resolvePath(UUID id) {
    return rootPath.resolve(id.toString()).normalize();
  }

  private MediaType resolveMediaType(String contentType) {
    if (contentType == null || contentType.isBlank()) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }

    try {
      return MediaType.parseMediaType(contentType);
    } catch (IllegalArgumentException e) {
      return MediaType.APPLICATION_OCTET_STREAM;
    }
  }

  private ContentDisposition buildContentDisposition(BinaryContentDto binaryContentDto) {
    boolean inline = binaryContentDto.contentType() != null
        && binaryContentDto.contentType().startsWith("image/");
    String fileName = binaryContentDto.fileName();

    ContentDisposition.Builder builder = inline
        ? ContentDisposition.inline()
        : ContentDisposition.attachment();

    if (fileName == null || fileName.isBlank()) {
      return builder.build();
    }

    return builder.filename(fileName, StandardCharsets.UTF_8).build();
  }
}