package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.auth.jwt.dto.JwtDto;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserRoleUpdateRequest;
import jakarta.validation.Valid;
import java.util.Map.Entry;

public interface AuthService {

  UserDto updateRole(@Valid UserRoleUpdateRequest userRoleUpdateRequest);

  Entry<JwtDto, String> republishToken(String refreshToken);
}
