package com.sprint.mission.discodeit.dto.data;

import com.sprint.mission.discodeit.entity.Role;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    Role role,
    BinaryContentDto profile,
    Boolean online
) {

}
