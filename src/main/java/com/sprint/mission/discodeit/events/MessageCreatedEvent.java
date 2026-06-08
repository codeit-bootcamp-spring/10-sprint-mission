package com.sprint.mission.discodeit.events;

import java.util.UUID;

public record MessageCreatedEvent(
    UUID messageId
) {

}
