package com.sprint.mission.discodeit.events;

import com.sprint.mission.discodeit.enums.Role;
import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    Role previousRole,
    Role newRole
) {

}
