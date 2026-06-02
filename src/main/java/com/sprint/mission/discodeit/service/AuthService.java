package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.JwtRefreshResult;
import com.sprint.mission.discodeit.dto.response.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;

public interface AuthService {

	JwtRefreshResult refresh(String refreshToken);

	UserDto updateRole(UserRoleUpdateRequest request);
}
