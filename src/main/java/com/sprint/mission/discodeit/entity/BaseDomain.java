package com.sprint.mission.discodeit.entity;

import java.util.UUID;

public class BaseDomain {
    protected UUID id;
    protected long createdAt;
    protected long updatedAt;

    public BaseDomain() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
}
