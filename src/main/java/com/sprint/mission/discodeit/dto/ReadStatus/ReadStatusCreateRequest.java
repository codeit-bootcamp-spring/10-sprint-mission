package com.sprint.mission.discodeit.dto.ReadStatus;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userID,
        UUID channelID
) {
}
