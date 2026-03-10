package com.sprint.mission.discodeit.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
public class BaseEntity implements Serializable {
    @Id
    private UUID id;
    @CreatedDate
    private Instant createdAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
    }
}
