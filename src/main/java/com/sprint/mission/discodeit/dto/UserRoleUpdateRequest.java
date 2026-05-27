package com.sprint.mission.discodeit.dto;

import com.sprint.mission.discodeit.security.Role;
import java.util.UUID;

public record UserRoleUpdateRequest(
        UUID userId,
        Role newRole
) {

}
