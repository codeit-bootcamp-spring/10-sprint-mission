package com.sprint.mission.discodeit.dto.binaryContent;

import com.sprint.mission.discodeit.entity.enums.BinaryContentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class BinaryContentDto {
    private UUID id;
    private long size;
    private String fileName;
    private String contentType;
    private BinaryContentStatus status;
}
