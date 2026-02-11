package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Setter
@Getter
public class BaseEntity implements Serializable {
    private static final long serialVersionUID =1L;

    protected UUID id;
    protected Instant createdAt;
    protected Instant updatedAt;

    protected BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
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

    public void touch() { // 수정 시점 갱신용 헬퍼
        this.updatedAt = Instant.ofEpochSecond(System.currentTimeMillis());
    }

}
