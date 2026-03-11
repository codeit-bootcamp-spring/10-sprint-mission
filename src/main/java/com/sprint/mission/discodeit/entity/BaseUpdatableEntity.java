package com.sprint.mission.discodeit.entity;

import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@MappedSuperclass
public class BaseUpdatableEntity extends BaseEntity{
    @LastModifiedDate
    protected Instant updatedAt;

    public BaseUpdatableEntity() {
        super();
    }
}
