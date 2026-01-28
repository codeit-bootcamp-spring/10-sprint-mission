package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginRequestDto;
import com.sprint.mission.discodeit.dto.UserDto;

@FunctionalInterface
public interface AuthService {
    UserDto login(LoginRequestDto request);
}
