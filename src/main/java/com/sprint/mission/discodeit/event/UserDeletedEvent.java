package com.sprint.mission.discodeit.event;

import com.sprint.mission.discodeit.dto.user.UserDto;
import java.util.UUID;

public record UserDeletedEvent(
    UUID id
) {


}
