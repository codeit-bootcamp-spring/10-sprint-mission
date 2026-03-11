package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@MappedSuperclass
@NoArgsConstructor
public abstract class BaseUpdatableEntity extends BaseEntity {
    @LastModifiedDate
    @Column
    private Instant updatedAt;
}
