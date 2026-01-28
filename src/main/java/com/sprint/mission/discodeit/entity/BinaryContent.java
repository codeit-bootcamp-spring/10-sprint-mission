package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Arrays;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final Instant createdAt;
    private final UUID profileId;
    private final UUID attachmentId;
    private final byte[] content;

    public BinaryContent(UUID profileId, UUID attachmentId, byte[] content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.profileId = profileId;
        this.attachmentId = attachmentId;
        this.content = content;
    }

    public byte[] getContent() {
        return Arrays.copyOf(content, content.length);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return id.equals(((BinaryContent) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
