package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Getter
public abstract class DefaultEntity {
    //공통필드
    protected final UUID id;
    protected final Instant createdAt;
    protected Instant updatedAt;

    public DefaultEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }
}
