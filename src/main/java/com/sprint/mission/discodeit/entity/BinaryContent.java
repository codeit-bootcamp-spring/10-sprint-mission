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
    private final byte[] bytes;
    private final String contentType;
    private final String filename;
    private final Instant createdAt;

    public BinaryContent(
            UUID ownerId,
            BinaryContentOwnerType binaryContentOwnerType,
            byte[] bytes,
            String contentType,
            String filename
    ) {
        validateContentType(contentType);

        this.contentType = contentType;
        this.filename = filename;
        this.id = UUID.randomUUID();
        this.ownerId = ownerId;
        this.binaryContentOwnerType = binaryContentOwnerType;
        this.bytes = bytes;

        this.createdAt = Instant.now();
    }

    private void validateContentType(String contentType) {
        if (!contentType.startsWith("image/")) {
            throw new IllegalArgumentException("이미지만 업로드 가능합니다");
        }
    }
}
