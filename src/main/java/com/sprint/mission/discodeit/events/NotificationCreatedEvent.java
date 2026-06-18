package com.sprint.mission.discodeit.events;

import com.sprint.mission.discodeit.dto.notificationdto.NotificationDto;
import java.util.List;
import java.util.UUID;

public record NotificationCreatedEvent(
    List<UUID> receiverIds,
    NotificationDto notification
) {

}
