package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.data.UserDto;
import com.sprint.mission.discodeit.dto.request.UserRoleUpdateRequest;
import com.sprint.mission.discodeit.jwt.TokenRefreshResult;
import org.springframework.security.access.prepost.PreAuthorize;

public interface AuthService {

  @PreAuthorize("hasRole('ADMIN')")
  UserDto updateRole(UserRoleUpdateRequest request);

  TokenRefreshResult refresh(String refreshToken);
}
