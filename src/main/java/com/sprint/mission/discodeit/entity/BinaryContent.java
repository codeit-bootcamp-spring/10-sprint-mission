package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentCreateRequest;
import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@ToString
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id = UUID.randomUUID();
    private final long createdAt = Instant.now().getEpochSecond();
    private final String fileName;
    private final String fileType;
    private final byte[] data;

    public BinaryContent(BinaryContentCreateRequest model) {
        this(model.fileName(), model.fileType(), model.data());
    }

    public BinaryContentResponse toResponse() {
        return BinaryContentResponse.builder()
                .id(id)
                .fileName(fileName)
                .fileType(fileType)
                .data(data)
                .build();
    }
}
