package com.sprint.mission.discodeit.event.user;

import com.sprint.mission.discodeit.dto.data.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserDeletedEvent {

  private final UserDto user;
}
