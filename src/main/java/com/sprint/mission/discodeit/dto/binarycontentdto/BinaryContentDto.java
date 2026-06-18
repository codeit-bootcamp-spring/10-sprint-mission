package com.sprint.mission.discodeit.dto.binarycontentdto;

import com.sprint.mission.discodeit.enums.binarycontents.BinaryContentStatus;
import java.util.UUID;

public record BinaryContentDto(
    UUID id,
    String fileName,
    long size,
    String contentType,
    BinaryContentStatus status,
    byte[] bytes
) {

}
