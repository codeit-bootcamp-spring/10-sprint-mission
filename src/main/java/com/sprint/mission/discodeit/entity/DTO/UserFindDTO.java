package com.sprint.mission.discodeit.entity.DTO;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

public record UserFindDTO(
        User user,
        UserStatus userStatus
) {
}
