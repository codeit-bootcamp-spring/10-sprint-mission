package com.sprint.mission.discodeit.entity;

import java.time.Instant;
import java.util.UUID;

public abstract class BaseEntity {
    protected final UUID id; // 객체 식별을 위한 id
    protected final Long createdAt; // 객체 생성 시간(유닉스 타임스탬프)
    protected Long updatedAt; // 객체 수정 시간(유닉스 타임스탬프)

    protected BaseEntity() {
        // id 초기화
        this.id = UUID.randomUUID();
        // 시간 초기화
        this.createdAt = Instant.now().toEpochMilli();
        this.updatedAt = this.createdAt;
    }

    // Getter
    public UUID getId() {
        return id;
    }

    public Long getCreatedAt() {
        return createdAt;
    }

    public Long getUpdatedAt() {
        return updatedAt;
    }

    // update 시간 메소드
    public void updateTime() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

}
