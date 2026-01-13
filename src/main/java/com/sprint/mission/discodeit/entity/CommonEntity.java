package com.sprint.mission.discodeit.entity;

import java.util.UUID;

abstract class CommonEntity {
    protected final UUID id;
    protected Long createdAt;
    protected Long updateAt;

    public CommonEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updateAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdateAt() {
        return updateAt;
    }
}