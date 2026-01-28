package com.sprint.mission.discodeit.dto.user;

import java.util.UUID;

public record UserInfoDto(
        UUID userId,
        String email,
        String userName,
        String nickName,
        String birthday,
        UUID profileId,
        boolean isOnline
) {
}
