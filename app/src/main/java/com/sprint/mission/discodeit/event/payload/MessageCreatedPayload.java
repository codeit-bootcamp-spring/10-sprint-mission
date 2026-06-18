package com.sprint.mission.discodeit.event.payload;

import com.sprint.mission.discodeit.dto.MessageDto;

public record MessageCreatedPayload(
    String channelName,
    MessageDto messageDto
) {

}
