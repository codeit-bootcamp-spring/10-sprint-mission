package com.sprint.mission.discodeit.event;

import java.util.UUID;

public final class NotificationEvents {
    private NotificationEvents() {}

    public record Created(UUID notificationId, UUID receiverId) {}
}
