package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@ToString
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private final long createdAt = Instant.now().getEpochSecond();
    private final String fileName;
    private final String fileType;
    private final byte[] data;

    public BinaryContentResponse toResponse() {
        return BinaryContentResponse.builder()
                .id(id)
                .fileName(fileName)
                .fileType(fileType)
                .data(data)
                .build();
    }
}
