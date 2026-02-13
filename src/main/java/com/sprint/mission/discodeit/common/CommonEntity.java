package com.sprint.mission.discodeit.common;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class CommonEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final UUID id;
    protected Instant createdAt;
    protected Instant updateAt;

    public CommonEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updateAt = this.createdAt;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        return id.equals(((CommonEntity) obj).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}