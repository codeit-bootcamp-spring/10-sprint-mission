package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Getter
public abstract class Entity implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public Entity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = createdAt;
    }

    public void update() {
        // 업데이트 시간 갱신
        this.updatedAt = System.currentTimeMillis();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Entity entity = (Entity) o;
        return Objects.equals(id, entity.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
