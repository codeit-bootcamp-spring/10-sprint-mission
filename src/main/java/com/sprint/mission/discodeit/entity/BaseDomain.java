package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class BaseDomain implements Serializable {
    protected final UUID id;
    protected Instant createdAt;
    protected Instant updatedAt;

    public BaseDomain() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
}
