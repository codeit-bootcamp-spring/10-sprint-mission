package com.sprint.mission.discodeit.dto.userStatus.response;

import java.util.UUID;

public record UserStatusResponse(
        UUID userStatusID,
        boolean status
) {
}
