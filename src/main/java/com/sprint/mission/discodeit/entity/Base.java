package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class Base implements Serializable {
    private static final long serialVersionUID = 1L; // (역)직렬화 클래스의 버전

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
