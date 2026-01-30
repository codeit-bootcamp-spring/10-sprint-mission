package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
public class UserStatus extends BaseEntity {
    private final UUID userId;
    private StatusType statusType;

    public UserStatus(User user) {
        super(UUID.randomUUID(), Instant.now());
        this.userId = user.getId();
        this.statusType = StatusType.ONLINE;
    }

    public boolean isOnline() {
        Instant now = Instant.now();
        Instant beforeFiveMinute = now.minus(Duration.ofMinutes(5));
        return updatedAt.isAfter(beforeFiveMinute);    // 마지막 접속 시간이 5분 전이면
    }

    public StatusType getStatusType(){
        if (isOnline()) {
            this.statusType = StatusType.ONLINE;
        }else{
            this.statusType = StatusType.OFFLINE;
        }
        return statusType;
    }

    // 마지막 접속 시간 갱신
    public void updateLastActiveTime(){
        updateUpdatedAt();
    }
}
