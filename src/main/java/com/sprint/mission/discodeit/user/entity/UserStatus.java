package com.sprint.mission.discodeit.user.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;

    private UUID userId;
    private Instant lastConnectedAt;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastConnectedAt = Instant.now();
    }

    public void updateConnection() {
        this.lastConnectedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean isConnected(){
        return lastConnectedAt != null &&
                (Instant.now().getEpochSecond() - lastConnectedAt.getEpochSecond() <= 300);
    }

}
