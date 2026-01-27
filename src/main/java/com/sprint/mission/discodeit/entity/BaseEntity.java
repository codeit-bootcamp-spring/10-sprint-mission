package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.UUID;

@Getter
public class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final UUID id;
    protected final Long createdAt;
    protected Long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    protected void setUpdatedAt() {
        this.updatedAt = System.currentTimeMillis();
    }
}
