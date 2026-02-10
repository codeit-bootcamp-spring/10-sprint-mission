package com.sprint.mission.discodeit.dto.binaryContent;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class BinaryContentResponseDto {
    private UUID id;
    private UUID userId;
    private UUID messageId;
    private byte[] bytes;
    private String fileName;
    private String contentType;
}
