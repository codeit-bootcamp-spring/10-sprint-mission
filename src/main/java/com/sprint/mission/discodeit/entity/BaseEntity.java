package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    protected final UUID id;
    protected final Instant createdAt;
    protected Instant updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();  // id 중복될 확률 매우매우 낮음 -> 현실적으로 0에 수렴함 // 1조 개의 UUID 중에 중복이 일어날 확률은 10억 분의 1
        Instant now = Instant.now();  // TODO: System.currentTimeMills() -> 이것과 차이점 정리
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void updateInstant() {
        this.updatedAt = Instant.now();
    }
}