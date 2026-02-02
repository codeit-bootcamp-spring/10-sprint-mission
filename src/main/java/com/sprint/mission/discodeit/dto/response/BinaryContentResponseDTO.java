package com.sprint.mission.discodeit.dto.response;

import com.sprint.mission.discodeit.entity.BinaryContentType;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

@Builder
public class BinaryContentResponseDTO {
    private UUID id;
    private Instant createdAt;
    private String fileName;
    private byte[] binaryContent;
    private BinaryContentType binaryContentType;
}
