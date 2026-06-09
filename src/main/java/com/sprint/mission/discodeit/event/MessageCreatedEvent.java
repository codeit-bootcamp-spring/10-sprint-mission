package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.entity.Message;

public record MessageCreatedEvent(
        Message message
) {
}
