package com.sprint.mission.discodeit.event.user;

import java.util.UUID;

public record UserDeletedEvent(
    UUID userId
) {

}
