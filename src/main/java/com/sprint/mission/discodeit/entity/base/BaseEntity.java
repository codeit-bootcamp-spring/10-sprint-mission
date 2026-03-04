package com.sprint.mission.discodeit.entity.base;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

@Getter
@Setter
@Entity
public abstract class BaseEntity {

    @Id
    @GeneratedValue
    public UUID id;

    @CreatedDate
    public Instant createdAt;


}