package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.dto.UserDto;

public interface AuthService {
    UserDto.Response login(LoginDto.LoginRequest request);

}
