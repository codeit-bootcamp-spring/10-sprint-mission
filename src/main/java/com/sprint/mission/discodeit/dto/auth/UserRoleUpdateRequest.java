package com.sprint.mission.discodeit.dto.auth;

import com.sprint.mission.discodeit.entity.Role;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record UserRoleUpdateRequest(
        @NotNull(message = "ID가 null입니다.")
        UUID userId,

        @NotNull(message = "권한이 null입니다.")
        Role newRole
) {
}
