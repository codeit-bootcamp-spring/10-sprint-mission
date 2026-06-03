package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.JwtRefreshDto;
import com.sprint.mission.discodeit.dto.auth.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;

public interface AuthService {

    UserDto updateUserRole(UserRoleUpdateRequest request);

    JwtRefreshDto refreshAccessToken(String refreshToken);
}
