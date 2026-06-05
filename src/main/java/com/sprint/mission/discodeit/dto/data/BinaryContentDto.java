package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.BinaryContentStatus;

import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    BinaryContentStatus status

) {

  public BinaryContentDto(UUID id, String fileName, Long size, String contentType) {
    this(id, fileName, size, contentType, BinaryContentStatus.SUCCESS);
  }
}
