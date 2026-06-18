package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authDto.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;

public interface AuthService {
    UserDto updateRole(UserRoleUpdateRequest request);
    boolean isOnline(UserDto dto);
}
