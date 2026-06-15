package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId,
    String channelName,
    MessageDto messageDto
) {

}
