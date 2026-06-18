package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.user.UserDto;

public record UserUpdatedEvent(
    UserDto userDto
) {


}
