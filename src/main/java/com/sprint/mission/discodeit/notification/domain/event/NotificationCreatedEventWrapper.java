package com.sprint.mission.discodeit.notification.domain.event;

import java.util.List;

public record NotificationCreatedEventWrapper(List<NotificationCreatedEvent> events) {

}
