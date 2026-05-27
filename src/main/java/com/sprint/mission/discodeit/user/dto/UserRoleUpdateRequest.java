package com.sprint.mission.discodeit.user.dto;

import com.sprint.mission.discodeit.user.Role;
import java.util.UUID;

public record UserRoleUpdateRequest(
    UUID userId,
    Role newRole
) {

}
