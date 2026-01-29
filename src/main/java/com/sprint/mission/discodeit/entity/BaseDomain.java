package com.sprint.mission.discodeit.entity;

import java.io.Serializable;
import java.util.UUID;

public class BaseDomain implements Serializable {
    protected final UUID id;
    protected long createdAt;
    protected long updatedAt;

    public BaseDomain() {
        this.id = UUID.randomUUID();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = this.createdAt;
    }
}
