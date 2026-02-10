package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serial;
import java.time.Instant;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class UserStatus extends BaseEntity{
    // 사용자가 생성될 때 함께 생성
    // 사용자별 접속 시간
    @Serial
    private static final long serialVersionUID = 1L;

    @Getter
    private final UUID userId;

    public enum Status {
        ONLINE, OFFLINE, AWAY
    }

    private Instant lastActiveTime = Instant.EPOCH;

    private Status status = Status.OFFLINE;

    // 상태 반환
    public Status getStatus() {
        if (!isOnline() && status != Status.OFFLINE) {
            return Status.AWAY;
        }
        return status;
    }

    // 온라인 판별 (300초 동안 활동 갱신 없으면 False)
    public boolean isOnline() {
        return lastActiveTime.isAfter(Instant.now().minusSeconds(300));
    }

    public void updateStatus(Status status) {
        this.status = status;
        this.updateTimestamp();
    }
    // 로그인
    public void userLogin() {
        status = Status.ONLINE;
        lastActiveTime = Instant.now();
        this.updateTimestamp();
    }

    // 활동 갱신
    public void userActive() {
        lastActiveTime = Instant.now();
        this.updateTimestamp();
    }

    // 로그아웃
    public void userLogout() {
        status = Status.OFFLINE;
        this.updateTimestamp();
    }


}
