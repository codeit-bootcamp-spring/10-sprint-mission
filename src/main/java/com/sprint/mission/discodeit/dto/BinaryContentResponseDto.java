package com.sprint.mission.discodeit.dto;

import java.util.UUID;

public record BinaryContentResponseDto(
    UUID id,
    String contentType,
    byte[] bytes
) {

}
