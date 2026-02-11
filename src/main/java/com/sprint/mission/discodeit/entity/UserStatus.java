package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;


//userStatus 는 유저 접속시간... 현재 접속중인지 관리
@Getter
public class UserStatus extends Basic implements Serializable {
    private static final long serialVersionUID = 1L;

    private final UUID userId;

    //마지막으로 확인된 접속 시간.
    private Instant lastLoginAt;

    public UserStatus(UUID userId) {
        super();
        if(userId == null) {
            throw new IllegalArgumentException("UserId는 null일수 없습니다.");
        }
        this.userId = userId;
        // 최초 생성
        this.lastLoginAt = this.createdAt;
        // 유저 최초 접속 시간
    }

    // 유저가 활동했을 때 호출 (메세지 전송, 채널 입장)
    public void refreshLogin() {
        this.lastLoginAt = Instant.now();
        update();
    }

    // 현재 온라인 상태 여부!
    // 유저가 활동한지 5분 이후 -> false
    public boolean isOnline() {
        return Duration.between(lastLoginAt, Instant.now()).toMinutes() < 5;
    }

}
