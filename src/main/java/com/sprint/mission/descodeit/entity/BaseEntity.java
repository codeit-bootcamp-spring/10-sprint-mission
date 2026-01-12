package com.sprint.mission.descodeit.entity;

import java.util.UUID;

public class BaseEntity {
    private final UUID id;
    private final Long createdAt;
    private Long updatedAt;

    public BaseEntity() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }

    public UUID getId(){
        return id;
    }

    public Long getCreatedAt(){
        return createdAt;
    }

    public Long getUpdatedAt(){
        return updatedAt;
    }

    public void updateTimeStamp(){
        updatedAt = System.currentTimeMillis();
    }

}
