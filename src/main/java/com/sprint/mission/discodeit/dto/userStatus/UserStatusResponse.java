package com.sprint.mission.discodeit.dto.userStatus;

import java.util.UUID;

public record UserStatusResponse(
        UUID userStatusID,
        boolean status
) {
}
