package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.message.MessageDto;
import java.time.Instant;

public record MessageCreatedEvent(
    MessageDto data,
    String channelName,
    Instant createdAt
) {


}
