package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.type.FileType;
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
    private byte[] data;
    private String fileName;
    private FileType fileType;
}
