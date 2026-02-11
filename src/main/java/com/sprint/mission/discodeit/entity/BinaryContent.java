package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
public class BinaryContent implements Serializable {
    private static final long serialVersionUID = 1L;
    private final UUID id;
    private final byte[] bytes;
    private final String fileName;
    private final long size;
    private final String contentType;
    private final Instant createdAt;

    public BinaryContent(byte[] bytes, String fileName, String contentType) {
        this.id = UUID.randomUUID();
        this.bytes = bytes;
        this.fileName = fileName;
        this.contentType = contentType;
        this.size = bytes.length;
        this.createdAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BinaryContent that = (BinaryContent) o;
        return java.util.Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }

    @Override
    public String toString() {
        return "BinaryContent{" +
                "id=" + id +
                ", fileName='" + fileName + '\'' +
                ", size=" + size +
                ", contentType='" + contentType + '\'' +
                '}';
    }
}