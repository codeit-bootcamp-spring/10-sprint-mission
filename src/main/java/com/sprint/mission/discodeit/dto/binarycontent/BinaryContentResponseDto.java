package com.sprint.mission.discodeit.dto.binarycontent;

import java.util.UUID;

public record BinaryContentResponseDto(
        UUID id,
        byte[] content
) {
}
