package com.sprint.mission.discodeit.dto.authdto;

import com.sprint.mission.discodeit.enums.Role;
import java.util.UUID;

public record RoleUpdateRequest(
    UUID userId,
    Role newRole
) {


}
