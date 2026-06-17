package com.sprint.mission.discodeit.binarycontent.dto;

import com.sprint.mission.discodeit.binarycontent.entity.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    Long size,
    String contentType,
    byte[] bytes,
    BinaryContentStatus status
) {

}
