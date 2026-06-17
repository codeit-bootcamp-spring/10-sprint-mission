package com.sprint.mission.discodeit.user.event;

import com.sprint.mission.discodeit.user.Role;
import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    Role oldRole,
    Role newRole
) {

}
