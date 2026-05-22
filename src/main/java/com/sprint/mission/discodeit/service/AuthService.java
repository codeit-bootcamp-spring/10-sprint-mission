package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import jakarta.validation.Valid;

public interface AuthService {

  UserDto updateRole(@Valid UserRoleUpdateRequest userRoleUpdateRequest);
}
