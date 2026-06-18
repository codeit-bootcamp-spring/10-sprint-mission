package com.sprint.mission.discodeit.event.user;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class UserCreateEvent {
    UserDto user;
}
