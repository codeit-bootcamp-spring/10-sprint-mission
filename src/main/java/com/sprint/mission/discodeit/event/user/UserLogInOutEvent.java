package com.sprint.mission.discodeit.event.user;

import com.sprint.mission.discodeit.dto.data.UserDto;

public record UserLogInOutEvent(
        UserDto user
) {

}
