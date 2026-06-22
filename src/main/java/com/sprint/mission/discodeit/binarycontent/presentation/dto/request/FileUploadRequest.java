package com.sprint.mission.discodeit.binarycontent.presentation.dto.request;

import com.sprint.mission.discodeit.global.infra.storage.exception.local.FileErrorCode;
import com.sprint.mission.discodeit.global.infra.storage.exception.local.FileException;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import org.springframework.web.multipart.MultipartFile;

public record FileUploadRequest(
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

  public static FileUploadRequest from(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    String filename = Objects.requireNonNull(file.getOriginalFilename(), "[error] name is null");
    String contentType = file.getContentType();
    long size = file.getSize();

    try {
      byte[] bytes = file.getBytes();
      return new FileUploadRequest(filename, contentType, size, bytes);
    } catch (IOException e) {
      Map<String, Object> details = Map.of("fileName", filename, "IOException", e.getMessage());
      throw new FileException(FileErrorCode.FILE_READ_ERROR, details);
    }
  }
}
