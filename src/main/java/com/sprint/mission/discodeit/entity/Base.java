package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public abstract class Base implements Serializable {
    // 필드
    private static final long serialVersionUID = 1L;
    @Getter
    private final UUID id;
    @Getter
    private final Instant createdAt;
    @Getter
    private Instant updatedAt;

    // 생성자
    public Base() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
    }
    // Setter
    public void updateUpdatedAt() {
        this.updatedAt = Instant.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Base base = (Base) o;
        return Objects.equals(id, base.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
