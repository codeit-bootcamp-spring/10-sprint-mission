package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class defaultEntity {
    protected final UUID id;
    protected final Long createAt;
    protected Long updatedAt;

    public defaultEntity() {
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
}
