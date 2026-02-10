package com.sprint.mission.discodeit.dto.auth.response;

import com.sprint.mission.discodeit.dto.user.response.UserCurrentStatusResponse;

import java.util.UUID;

public record AuthServiceResponse(
        UUID userID,
        String name,
        UserCurrentStatusResponse userStatus
) {
}
