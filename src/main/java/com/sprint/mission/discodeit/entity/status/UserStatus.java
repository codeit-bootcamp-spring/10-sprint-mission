package com.sprint.mission.discodeit.entity.status;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    // Getters
    private final UUID id;
    private final UUID userId;
    private Instant lastSeenAt;
    private final Instant createdAt;
    private Instant updatedAt;
    private String status; // "ONLINE","OFFLINE","AWAY"
    private Instant lastActiveAt;

    public UserStatus(UUID userId,String status,Instant lastActiveAt) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.status = status;
        this.lastActiveAt = lastActiveAt;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

//    public static UserStatus create(UUID userId) {
//        Instant now = Instant.now();
//        return new UserStatus(id, userId, now, now, now);
//    }

    public void updateLastActiveAt(Instant now) {
        this.lastSeenAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void updateStatus(String status) {
        this.status = null;
    }
    /*
     * 마지막 접속 시간이 현재로부터 5분 이내이면 온라인으로 판단
     */
    public boolean isOnline() {
        Instant fiveMinutesAgo = Instant.now().minus(Duration.ofMinutes(5));
        return lastSeenAt.isAfter(fiveMinutesAgo);
    }

}
