package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.role.Role;

import java.util.UUID;

public record RoleUpdateRequest(
        UUID userId,
        Role newRole
) {
}
