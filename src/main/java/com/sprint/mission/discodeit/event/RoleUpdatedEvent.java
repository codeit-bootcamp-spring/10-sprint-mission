package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

// 권한 변경 이벤트
public record RoleUpdatedEvent(
    UUID userId,
    Role oldRole,
    Role newRole
) {

}
