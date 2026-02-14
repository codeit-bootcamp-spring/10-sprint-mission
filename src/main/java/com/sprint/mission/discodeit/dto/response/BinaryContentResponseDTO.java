package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Builder
public class BinaryContentResponseDTO {
    private UUID id;
    private Instant createdAt;
    private String fileName;
    private byte[] binaryContent;
    private BinaryContentType binaryContentType;
}
