package com.sprint.mission.discodeit.entity;


import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final UUID ownerId;
    private final BinaryContentOwnerType binaryContentOwnerType;
    private final byte[] image;
    private final Instant createdAt;

    public BinaryContent(
            UUID ownerId,
            BinaryContentOwnerType binaryContentOwnerType,
            byte[] image
    ) {
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.binaryContentOwnerType = binaryContentOwnerType;
        this.image = image;

        this.createdAt = Instant.now();
    }
}
