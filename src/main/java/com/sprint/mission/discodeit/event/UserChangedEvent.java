package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.UserDto;

public record UserChangedEvent(String action, UserDto dto) {
}
