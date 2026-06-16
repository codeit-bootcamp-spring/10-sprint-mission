package com.sprint.mission.discodeit.events;

import com.sprint.mission.discodeit.dto.userdto.UserDto;

public record UserDeletedEvent(
    UserDto user
) {

}
