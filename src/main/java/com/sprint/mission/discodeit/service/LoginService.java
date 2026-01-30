package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;

public interface LoginService {
    UserResponseDto login(UserLoginRequestDto dto);
}
