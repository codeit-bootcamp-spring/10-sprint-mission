package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.event.enums.ChangeType;
import lombok.Getter;

import java.time.Instant;

// 사용자 정보 또는 로그인 상태 변경 시 알림 발생을 요청하는 이벤트 클래스
@Getter
public class UserChangeEvent {

    private final ChangeType changeType;
    private final UserDto userDto;

    // 이벤트 생성 시간
    private final Instant occurredAt;

    // 이벤트 생성자
    public UserChangeEvent(ChangeType changeType, UserDto userDto) {
        this.changeType = changeType;
        this.userDto = userDto;

        this.occurredAt = Instant.now();
    }
}
