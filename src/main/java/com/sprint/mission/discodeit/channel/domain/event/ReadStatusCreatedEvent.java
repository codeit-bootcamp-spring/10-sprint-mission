package com.sprint.mission.discodeit.channel.domain.event;

import java.util.List;
import java.util.UUID;

public record ReadStatusCreatedEvent(UUID channelId,
                                     List<UUID> participantIds,
                                     boolean notificationEnabled) {

}
