package com.sprint.mission.discodeit.event.sse;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import java.util.List;

public record NotificationCreatedEvent(
    List<NotificationDto> notificationDtos
) {

}
