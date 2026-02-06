package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record LoginResponseDto(
        UUID id,
        String username,
        String password
) {
}
