package com.sprint.mission.discodeit.dto.auth;

import java.util.UUID;

public record AuthResponseDto(UUID userId,
                              String userName,
                              String message) {
}
