package com.sprint.mission.discodeit.dto.userstatus;

import java.util.UUID;

public record UserStatusUpdateDTO(
        UUID userStatusId,
        boolean isOnline
) {
}
