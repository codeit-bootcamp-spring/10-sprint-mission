package com.sprint.mission.discodeit.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Getter
@EqualsAndHashCode(of = "id") // ID가 같으면 같은 객체로 인식하도록 설정
public abstract class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    protected final UUID id;
    protected final Long createdAt;
    protected Long updatedAt;

    protected BaseEntity(){
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    protected void updated(){
        this.updatedAt = System.currentTimeMillis();
    }
}
