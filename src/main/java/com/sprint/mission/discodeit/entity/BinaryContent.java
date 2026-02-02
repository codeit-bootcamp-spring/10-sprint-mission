package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final String fileName;
    private final byte[] data;
    private final Instant createdAt;

    public BinaryContent(UUID id, String fileName, byte[] data, Instant createdAt) {
        this.id = id;
        this.fileName = fileName;
        this.data = data;
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BinaryContent)) return false;
        BinaryContent that = (BinaryContent) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
