package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import com.sprint.mission.discodeit.entity.User;

public record RoleUpdatedEvent(
        User user,
        Role oldRole,
        Role newRole
) {
}
