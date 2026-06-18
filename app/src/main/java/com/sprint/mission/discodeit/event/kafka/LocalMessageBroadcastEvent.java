package com.sprint.mission.discodeit.event.kafka;

import com.sprint.mission.discodeit.dto.message.MessageDto;

public record LocalMessageBroadcastEvent(
    MessageDto messageDto
) {

}
