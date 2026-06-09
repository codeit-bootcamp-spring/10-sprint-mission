package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.auth.enums.Role;
import com.sprint.mission.discodeit.dto.binarycontent.BinaryContentDto;
import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    String username,
    String email,
    Role role,
    BinaryContentDto profile,
    Boolean online,
    Instant createdAt,
    Instant updatedAt
) {

}
