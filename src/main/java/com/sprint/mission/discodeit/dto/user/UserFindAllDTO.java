package com.sprint.mission.discodeit.dto.user;

import com.sprint.mission.discodeit.entity.User;
import com.sprint.mission.discodeit.entity.UserStatus;

import java.util.List;

public record UserFindAllDTO(
        List<User> userList,
        List<UserStatus> userStatusList
) {
}
