package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.common.util.TimeConverter;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentServiceDTO.BinaryContentResponse;
import com.sprint.mission.discodeit.dto.binarycontent.request.BinaryContentCreateRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
public class BinaryContent implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id = UUID.randomUUID();
    private final Instant createdAt = Instant.now();
    private final String fileName;
    private final byte[] data;

    public BinaryContent(BinaryContentCreateRequest model) {
        this(model.fileName(), model.data());
    }

    public BinaryContentResponse toResponse() {
        return BinaryContentResponse.builder()
                .id(id)
                .fileName(fileName)
                .data(data)
                .createdAt(TimeConverter.toDateTime(createdAt))
                .build();
    }
}
