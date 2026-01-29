package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    private final UUID id;
    private final Instant createdAt;
    private Instant updatedAt;

    private final UUID userId;
    private Instant accessTime;
    private Status status;

    public UserStatus(UUID userId, Status status) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = createdAt;
        this.userId = userId;
        this.status = status;
        this.accessTime = Instant.now();
    }

    public void changeStatus(Status status) {
        if (status == null) {
            throw new RuntimeException();
        }
        this.status = status;
        update();
    }

    public void touch() {
        this.accessTime = Instant.now();
        update();
    }

    public boolean isCurrentlyLoggedIn() {
        Instant now = Instant.now();
        Duration sinceLastAccess = Duration.between(accessTime, now);
        if (sinceLastAccess.isNegative()) {
            return true;
        }
        return sinceLastAccess.compareTo(Duration.ofMinutes(5)) <= 0;
    }

    public void refreshAutoPresence() {
        if (!isCurrentlyLoggedIn()) {
            if (status != Status.OFFLINE) {
                status = Status.OFFLINE;
                update();
            }
        }
    }

    public void update() {
        // 업데이트 시간 갱신
        this.updatedAt = Instant.now();
    }

    @Getter
    public enum Status {
        ONLINE("online", "온라인"),
        OFFLINE("offline", "오프라인"),
        INVISIBLE("invisible", "오프라인"),
        IDLE("idle", "자리 비움"),
        DO_NOT_DISTURB("dnd", "방해 금지");

        private final String userStatus;
        private final String displayName;

        Status(String userStatus, String displayName) {
            this.userStatus = userStatus;
            this.displayName = displayName;
        }
    }
}
