package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;

public interface AuthService {

    UserDto updateUserRole(UserRoleUpdateRequest request);
}
