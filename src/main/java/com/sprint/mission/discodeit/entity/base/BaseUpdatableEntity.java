package com.sprint.mission.discodeit.entity.base;

import java.time.Instant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
public abstract class BaseUpdatableEntity extends BaseEntity {

    @LastModifiedDate
    public Instant updatedAt;


}