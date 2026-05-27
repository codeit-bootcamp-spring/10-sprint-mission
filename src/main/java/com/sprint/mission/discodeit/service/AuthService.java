package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.dto.UserDto.UserRoleUpdateRequest;

public interface AuthService {

  UserDto updateRole(UserRoleUpdateRequest request);

  UserDto updateRoleInner(UserRoleUpdateRequest request);

}
