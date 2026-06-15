package com.sprint.mission.discodeit.user.domain.event;

import java.util.UUID;

public record UserCreatedEvent(UUID id, String password) {

}
