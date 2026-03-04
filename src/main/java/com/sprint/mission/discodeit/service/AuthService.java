package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDto;

public interface AuthService {
    UserDto.userResponse login(UserDto.userLoginRequest loginReq);
}
