package com.sprint.mission.discodeit.dto.request;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record UserRoleUpdateRequest(
    @NotNull(message = "userId is required.")
    UUID userId,

    @NotNull(message = "newRole is required.")
    Role role
) {
}
