package com.sprint.mission.discodeit.event;

import java.util.UUID;

public record UserOnlineStatusChangedEvent(
    UUID userId
) {

}
