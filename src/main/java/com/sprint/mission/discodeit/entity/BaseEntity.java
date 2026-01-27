package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
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

    // update 시간 메소드
    public void updateTime() {
        this.updatedAt = Instant.now().toEpochMilli();
    }

}
