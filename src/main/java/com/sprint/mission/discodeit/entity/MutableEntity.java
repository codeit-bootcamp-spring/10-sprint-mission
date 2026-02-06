package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class MutableEntity extends BaseEntity {
    private Instant updatedAt;

    MutableEntity() {
        super();
    }

    public void updateUpdatedAt() {
        this.updatedAt = Instant.now();
    }
}
