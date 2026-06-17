package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    long size,
    String contentType,
    BinaryContentStatus status
) {

  public record BinaryContentCreateRequest(String contentType, String filename, byte[] bytes) {

  }
}
