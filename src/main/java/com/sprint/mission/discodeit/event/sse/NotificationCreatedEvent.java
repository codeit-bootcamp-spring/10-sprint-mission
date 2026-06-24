package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.data.NotificationDto;

public record NotificationCreatedEvent(
    NotificationDto notification
) {

}
