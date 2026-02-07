package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.UUID;

@Getter
public class BaseEntity implements Serializable {
    private final UUID id;
    private final Instant createdAt;

    @Setter
    private Instant updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now().atZone(ZoneId.of("Asia/Seoul")).toInstant();
        this.updatedAt = Instant.now().atZone(ZoneId.of("Asia/Seoul")).toInstant();
    }


}
