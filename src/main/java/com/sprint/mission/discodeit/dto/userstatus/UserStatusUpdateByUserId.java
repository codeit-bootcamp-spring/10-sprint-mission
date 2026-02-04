package com.sprint.mission.discodeit.dto.userstatus;

import java.util.UUID;

public record UserStatusUpdateByUserId(
        UUID userId,
        boolean isOnline
) {
}
