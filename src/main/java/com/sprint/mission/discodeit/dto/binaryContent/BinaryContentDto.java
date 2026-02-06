package com.sprint.mission.discodeit.dto.binaryContent;

import java.util.UUID;

public record BinaryContentDto(
        byte[] bytes,
        UUID binaryContentId
) {
}
