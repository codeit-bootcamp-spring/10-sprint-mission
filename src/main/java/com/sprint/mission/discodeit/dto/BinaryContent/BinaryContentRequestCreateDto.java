package com.sprint.mission.discodeit.dto.BinaryContent;

public record BinaryContentRequestCreateDto(byte[] data,
                                            String contentType) {
}
