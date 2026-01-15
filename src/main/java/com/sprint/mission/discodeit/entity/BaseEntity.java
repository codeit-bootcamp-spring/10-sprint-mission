package com.sprint.mission.discodeit.entity;


import com.sprint.mission.discodeit.service.UserService;

import java.util.*;

public abstract class BaseEntity {
    // 모든 엔티티가 공통으로 가지는 필드
    protected final UUID id;
    protected final Long createdAt;
    protected Long updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = null;
    }

    protected void touch() {
        this.updatedAt = System.currentTimeMillis();
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
}
