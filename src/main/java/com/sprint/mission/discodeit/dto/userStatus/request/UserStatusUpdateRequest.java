package com.sprint.mission.discodeit.dto.userStatus.request;

import java.util.UUID;

public record UserStatusUpdateRequest(
        UUID userID,
        boolean status
) {
}
