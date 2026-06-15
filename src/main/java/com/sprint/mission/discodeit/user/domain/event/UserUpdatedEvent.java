package com.sprint.mission.discodeit.user.domain.event;

import java.util.UUID;

public record UserUpdatedEvent(UUID id, String password) {

}
