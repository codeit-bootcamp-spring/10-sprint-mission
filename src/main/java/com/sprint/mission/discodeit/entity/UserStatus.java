package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.sprint.mission.discodeit.entity.UserStatusType.*;

@Getter
public class UserStatus extends BaseEntity {
    private UUID userId;                    // 사용자 고유 id (변경 불가능)
    private UserStatusType status;          // 사용자 접속 상태 (변경 가능)
    private Instant lastOnelineTime;        // 마지막 접속 시간 (변경 가능)

    public UserStatus(UUID userId) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.status = UserStatusType.OFFLINE;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        this.lastOnelineTime = Instant.now();
    }

    public void isCurrentlyOnline() {       // 5분 전 이내 접속 이력이 있다면, 온라인 상태 유지
        boolean fiveMinutesAgo = Instant.now()
                .minus(5, ChronoUnit.MINUTES)
                .isBefore(lastOnelineTime);

        this.status = fiveMinutesAgo ? ONLINE : OFFLINE;
    }

    public void updateLastOnlineTime() {
        this.lastOnelineTime = Instant.now();
    }

    public void updateStatus(UserStatusType status) {
        this.status = status;
    }
}
