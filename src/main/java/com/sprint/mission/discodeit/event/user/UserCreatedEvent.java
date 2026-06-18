package com.sprint.mission.discodeit.event.user;

import com.sprint.mission.discodeit.dto.user.UserDto;

public record UserCreatedEvent(
    UserDto user
) {

}
