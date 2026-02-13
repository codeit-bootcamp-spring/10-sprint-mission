package com.sprint.mission.discodeit.dto.user.response;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        String username,
        String nickName,
        String birthday,
        UUID profileId
) {
}
