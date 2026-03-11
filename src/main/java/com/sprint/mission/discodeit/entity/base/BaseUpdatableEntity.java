package com.sprint.mission.discodeit.entity.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@Getter
@Setter
public abstract class BaseUpdatableEntity extends BaseEntity{
    @LastModifiedDate
    private Instant updatedAt;
}
