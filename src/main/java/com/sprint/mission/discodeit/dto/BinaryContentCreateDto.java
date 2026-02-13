package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentCreateDto(
        String contentType,
        byte[] content

) {
}
