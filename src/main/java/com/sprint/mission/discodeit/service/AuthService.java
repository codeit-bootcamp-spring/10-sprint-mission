package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;

public interface AuthService {
    UserDto.response login(UserDto.LoginRequest loginReq);
}
