package com.sprint.mission.discodeit.dto.BinaryContent;

public record BinaryContentResponseDto(byte[] bytes,
                                       String contentType) {
}
