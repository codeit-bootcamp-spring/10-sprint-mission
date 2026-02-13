package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserResponseDto;
import com.sprint.mission.discodeit.dto.UserLoginDto;

public interface AuthService {
    UserResponseDto login(UserLoginDto request);
}
