package com.sprint.mission.discodeit.binarycontent;

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
    private final byte[] content;

    public BinaryContent(byte[] content) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
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
