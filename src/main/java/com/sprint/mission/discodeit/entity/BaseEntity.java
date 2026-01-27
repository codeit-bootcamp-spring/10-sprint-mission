package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;
@Getter
abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final UUID id;
    protected final Long createdAt;
    protected Long updatedAt;

    protected BaseEntity(UUID id, Long createdAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void updateUpdatedAt(Long updatedAt) {
        this.updatedAt = updatedAt;
    }

}
