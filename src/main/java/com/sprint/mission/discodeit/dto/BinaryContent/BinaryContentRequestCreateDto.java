package com.sprint.mission.discodeit.dto.BinaryContent;

public record BinaryContentRequestCreateDto(byte[] bytes,
                                            String contentType) {
}
