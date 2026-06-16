package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public final class UserEvents {
    private UserEvents() {}

    public record Updated(UUID userId) {}
    public record StatusUpdated(UUID userId) {}
    public record RoleUpdated(UUID userId, Role oldRole, Role newRole) {}
}
