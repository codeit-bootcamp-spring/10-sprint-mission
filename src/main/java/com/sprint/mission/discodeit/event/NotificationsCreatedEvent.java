package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.notification.NotificationDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class NotificationsCreatedEvent {
    private final List<NotificationDto> dtos;
}
