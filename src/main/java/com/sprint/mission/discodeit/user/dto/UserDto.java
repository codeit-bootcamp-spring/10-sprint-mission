package com.sprint.mission.discodeit.user.dto;

import com.sprint.mission.discodeit.binarycontent.dto.BinaryContentDto;
import com.sprint.mission.discodeit.user.Role;
import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    Instant createdAt,
    Instant updatedAt,
    String username,
    String email,
    BinaryContentDto profile,
    Role role,
    boolean online) {

}
