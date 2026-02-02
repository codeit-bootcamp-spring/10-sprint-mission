package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public class BaseEntity {
    protected UUID id;
    protected Instant createdAt;
    protected Instant updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.ofEpochSecond(System.currentTimeMillis());
        this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }

//    public Long getUpdatedAt() {
//        return updatedAt;
//    }
//
//    public void setUpdatedAt(Long updatedAt) {
//        this.updatedAt = updatedAt;
//    }
//
//    public Long getCreatedAt() {
//        return createdAt;
//    }
//
//    public UUID getId() {
//        return id;
//    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void touch() { // 수정 시점 갱신용 헬퍼
        this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }

}
