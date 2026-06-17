package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.exception.common.InvalidInputException;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

// 권한 변경시 알림 발생을 요청하는 이벤트 클래스
@Getter
public class RoleUpdatedEvent {

    private final UUID userId;
    private final Role oldRole;
    private final Role newRole;

    // 이벤트 생성 시간
    private final Instant occurredAt;

    // 이벤트 생성자
    public RoleUpdatedEvent(
            UUID userId,
            Role oldRole,
            Role newRole
    ) {
        if (userId == null) {
            throw new InvalidInputException("userId", null);
        }
        if (oldRole == null) {
            throw new InvalidInputException("oldRole", null);
        }
        if (newRole == null) {
            throw new InvalidInputException("newRole", null);
        }

        this.userId = userId;
        this.oldRole = oldRole;
        this.newRole = newRole;

        this.occurredAt = Instant.now();
    }
}
