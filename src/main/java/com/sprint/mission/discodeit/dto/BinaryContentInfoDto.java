package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentInfoDto(
        UUID id,
        byte[] content
) {
}
