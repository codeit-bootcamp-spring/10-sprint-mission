package com.sprint.mission.discodeit.event.notification;

import com.sprint.mission.discodeit.dto.data.NotificationDto;
import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationCreatedEvent {

  private final NotificationDto notification;
  private final UUID receiverId;
}
