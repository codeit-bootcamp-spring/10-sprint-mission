package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.exception.file.FileErrorCode;
import com.sprint.mission.discodeit.exception.file.FileException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.web.multipart.MultipartFile;

public record FileUploadDto(
    String fileName,
    String contentType,
    Long size,
    byte[] bytes
) {

  @Override
  public String toString() {
    return "FileUploadDto{" +
        "fileName='" + fileName + '\'' +
        ", contentType='" + contentType + '\'' +
        ", size=" + size +
        '}';
  }

  public static Optional<FileUploadDto> from(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return Optional.empty();
    }

    String filename = Objects.requireNonNull(file.getOriginalFilename(), "[error] name is null");
    String contentType = file.getContentType();
    long size = file.getSize();

    try {
      byte[] bytes = file.getBytes();
      return Optional.of(new FileUploadDto(filename, contentType, size, bytes));
    } catch (IOException e) {
      Map<String, Object> details = Map.of("fileName", filename, "IOException", e.getMessage());
      throw new FileException(FileErrorCode.FILE_CANT_READ, details);
    }
  }
}
