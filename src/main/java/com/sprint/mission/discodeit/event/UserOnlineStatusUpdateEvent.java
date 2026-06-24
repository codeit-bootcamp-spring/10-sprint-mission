package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.user.UserDto;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// 사용자 온라인 상태 변경 시 알림 발생을 요청하는 이벤트 클래스
@Getter
public class UserOnlineStatusUpdateEvent {

    private final UUID userId;
    private final UserDto userDto;

    // 이벤트 생성 시간
    private final Instant occurredAt;

    // 이벤트 생성자
    public UserOnlineStatusUpdateEvent(
            UUID userId,
            UserDto userDto
    ) {
        this.userId = userId;
        this.userDto = userDto;

        this.occurredAt = Instant.now();
    }
}
