package com.sprint.mission.discodeit.entity;

import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@SuperBuilder
@AllArgsConstructor
public abstract class BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final UUID id;
    protected final Instant createdAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
    }

    // 공통 Getter
    public UUID getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }

}
