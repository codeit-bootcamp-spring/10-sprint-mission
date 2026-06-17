package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.UserRole;
import java.util.UUID;

// on(RoleUpdatedEvent) - 사용자 역할 변경 시 이벤트 발행
public record RoleUpdatedEvent(
    UUID userId,
    UserRole oldRole,
    UserRole newRole
) {

}
