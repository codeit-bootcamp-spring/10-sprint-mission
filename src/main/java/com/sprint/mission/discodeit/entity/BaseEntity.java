package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }

    protected void setUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
