package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends Basic implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID userId;
    private Instant lastLoginAt;

    public UserStatus(UUID userId) {
        super();
        if(userId == null) {
            throw new IllegalArgumentException("UserId는 null일수 없습니다.");
        }
        this.userId = userId;
        this.lastLoginAt = this.createdAt;
        // 유저 접속 시간
    }
    public void refreshLogin() {
        this.lastLoginAt = Instant.now();
        update();
    }

    // 유저가 활동한지 5분 이후 -> false
    public boolean isOnline() {
        return Duration.between(lastLoginAt, Instant.now()).toMinutes() < 5;
    }



}
