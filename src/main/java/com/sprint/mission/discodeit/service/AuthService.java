package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserLoginDto;
import com.sprint.mission.discodeit.dto.user.UserDto;

public interface AuthService {
    UserDto login(UserLoginDto dto);
}
