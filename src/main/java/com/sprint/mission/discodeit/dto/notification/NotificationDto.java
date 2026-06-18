package com.sprint.mission.discodeit.dto.notification;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
public class NotificationDto {
    private UUID id;
    private Instant createdAt;
    private UUID receiverId;
    private String title;
    private String content;
}
