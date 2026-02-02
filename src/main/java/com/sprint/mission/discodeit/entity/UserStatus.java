package com.sprint.mission.discodeit.entity;

import lombok.Getter;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus {
    private UUID id;
    private Instant createdAt;
    private Instant updatedAt;
    //
    private Instant lastActiveAt;  //마지막 접속 시간
    private UUID userId;     //연결된 유저 ID
    private boolean isOnline;

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastActiveAt = Instant.now();
        this.userId = userId;
        this.isOnline = false;
    }

    //활동 로그 업데이트
    public void updateStatus() {
        this.lastActiveAt = Instant.now();
        this.updatedAt = Instant.now();
    }
    //참이면 온라인, 거짓이면 오프라인
    public boolean isOnline() {
        return lastActiveAt.isAfter(Instant.now().minusSeconds(5 * 60));
    }
}
