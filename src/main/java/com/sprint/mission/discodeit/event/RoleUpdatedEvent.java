package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.auth.enums.Role;
import java.util.UUID;

public record RoleUpdatedEvent(
    UUID userId,
    Role beforeRole,
    Role afterRole
) {

}
