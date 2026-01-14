package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseEntity {
    protected UUID id;
    protected long createdAt;
    protected long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public UUID getId() {
        return id;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void touch() { // 수정 시점 갱신용 헬퍼
        this.updatedAt = System.currentTimeMillis();
    }


}
