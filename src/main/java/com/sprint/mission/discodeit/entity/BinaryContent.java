package com.sprint.mission.discodeit.entity;

import com.sprint.mission.discodeit.dto.BinaryContentServiceDTO.BinaryContentResponse;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@AllArgsConstructor
@ToString
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id = UUID.randomUUID();
    private final long createdAt = Instant.now().getEpochSecond();
    @Setter
    private String url;

    public BinaryContentResponse toResponse() {
        return new BinaryContentResponse(id, url);
    }
}
