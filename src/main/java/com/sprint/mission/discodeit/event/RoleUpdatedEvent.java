package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Role;

import java.util.UUID;

/*
    RoleUpdatedEvent
    ----------------

 */
public record RoleUpdatedEvent(
        UUID userId,
        Role oldRole,
        Role newRole
) {
}