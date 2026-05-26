package com.sprint.mission.discodeit.dto.authDto;

import com.sprint.mission.discodeit.entity.enums.Role;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserRoleUpdateRequest {
    @NotNull
    private UUID userId;

    @NotNull
    private Role newRole;
}
