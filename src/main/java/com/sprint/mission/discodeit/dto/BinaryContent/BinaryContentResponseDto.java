package com.sprint.mission.discodeit.dto.BinaryContent;

public record BinaryContentResponseDto(byte[] data,
                                       String contentType) {
}
