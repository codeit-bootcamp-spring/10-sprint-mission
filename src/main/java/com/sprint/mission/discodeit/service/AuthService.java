package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthDto.UserLoginRequestDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;

public interface AuthService {
    UserResponseDto login(UserLoginRequestDto dto);
}
