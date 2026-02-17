package com.sprint.mission.discodeit.message.dto;

import java.util.UUID;

public record ReadStatusCreateRequest(
        UUID userId,
        UUID channelId
                                      ) {
}
