package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.io.Serial;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * 사용자별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델로,
 * 사용자의 온라인 상태를 확인하기 위해 사용
 * <br>
 * 마지막 접속 시간을 기준으로 현재 로그인한 유저를 판단
 * <br>
 * 마지막 접속 시간이 현재 시간 기준 5분 이내라면 접속 중인 유저로 간주
 */
@Getter
public class UserStatus extends BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UUID userId;
    private Instant lastOnlineTime;

    // 생성자
    // 유저 생성 시 함께 생성?
    public UserStatus(UUID userId) {
        this.userId = userId;
        this.lastOnlineTime = Instant.EPOCH; // 생성 시, Online 상태로 보지 않음
    }

    // update
    // 유저 온라인 상태 주기적? 업데이트?
    public void updateLastOnlineTime(Instant lastOnlineTime) {
        this.lastOnlineTime = lastOnlineTime;
        updateTime();
    }

    // 현재 유저 상태 확인(자리 비움, 미접속 등등)
    // 상대도 확인 가능 -> `updateTime()` 미사용이 맞을 것 같음
    public boolean isOnlineStatus() {
        return !Instant.now().isAfter(lastOnlineTime.plus(Duration.ofMinutes(5)));
        // !true = 5분 초과
        // !false = 5분 포함 이내
    }
}
