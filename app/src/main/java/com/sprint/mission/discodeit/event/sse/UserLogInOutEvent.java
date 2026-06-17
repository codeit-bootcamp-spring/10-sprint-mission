package com.sprint.mission.discodeit.event.sse;

import java.util.UUID;

public record UserLogInOutEvent(
    UUID userId,
    boolean isOnline
) {

}
