package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.type.FileType;
import lombok.Getter;

import java.util.UUID;

@Getter
public class BinaryContentCreateDto {
    private UUID userId;
    private UUID messageId;
    private String name;
    private FileType fileType;
    private byte[] fileData;
}
