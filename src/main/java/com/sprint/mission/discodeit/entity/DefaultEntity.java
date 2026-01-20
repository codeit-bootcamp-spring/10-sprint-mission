package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public abstract class DefaultEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final UUID id;
    protected final Long createAt;
    protected Long updatedAt;

    public DefaultEntity() {
        this.id = UUID.randomUUID();
        this.createAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public UUID getId() {
        return id;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultEntity that = (DefaultEntity) o;
        return getId().equals(that.getId());
    }

    public int hashCode() {
        return java.util.Objects.hash(getId());
    }
}

