package com.sprint.mission.discodeit.entity;

import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * UserStatus
 * 사용자 별 마지막으로 확인된 접속 시간을 표현하는 도메인 모델. 사용자의 온라인 상태를 확인하기 위해 활용
 */

@Getter
public class UserStatus extends BaseEntity {
    private final UUID userId;

    public UserStatus(UUID userId) {
        super();
        this.userId = userId;
    }

    // 활동 시간 갱신
    public void renewActivity() {
        updateInstant();
    }

    // 현재 '온라인' 상태인지 확인 (5분 이내 활동 시 true)
    // TODO: 온라인 / 자리 비움 / 방해 금지 / 오프라인 표시 / 오프라인 -> Enum 으로 상태 구현
    public boolean isOnline() {
        Instant now = Instant.now();
        Duration duration = Duration.between(this.updatedAt, now);
        return duration.toMinutes() <= 5; // 마지막 접속 시간이 현재 시간으로부터 5분 '이내'이면 현재 접속 중인 유저로 간주
    }

    // 테스트용 -> 시간 주입 받는 메서드
    public boolean isOnline(Instant current) {
        Duration duration = Duration.between(this.updatedAt, current);
        return duration.toMinutes() <= 5;
    }
}
