package com.sprint.mission.discodeit.event.kafka;

import java.util.UUID;

public record KafkaUserLogInOutEvent(
    UUID userId,
    boolean isOnline
) {

}
