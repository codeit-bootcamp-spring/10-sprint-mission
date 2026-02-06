package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserResponse(
        UUID userId,
        String email,
        String userName,
        String nickName,
        String birthday,
        UUID profileId
) {
}
