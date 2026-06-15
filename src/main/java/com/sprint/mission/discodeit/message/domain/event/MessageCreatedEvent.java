package com.sprint.mission.discodeit.message.domain.event;

import java.util.UUID;
import lombok.Builder;

@Builder
public record MessageCreatedEvent(UUID senderId,
                                  UUID channelId,
                                  String content) {

}
