package com.sprint.mission.discodeit.dto.auth.response;

import com.sprint.mission.discodeit.dto.user.response.UserStatusResponse;

import java.util.UUID;

public record AuthServiceResponse(
        UUID userID,
        String name,
        UserStatusResponse userStatus
) {
}
