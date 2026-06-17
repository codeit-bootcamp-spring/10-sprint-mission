package com.sprint.mission.discodeit.message.event;

import com.sprint.mission.discodeit.message.entity.Message;

public record MessageCreatedEvent(
    Message message
) {

}
