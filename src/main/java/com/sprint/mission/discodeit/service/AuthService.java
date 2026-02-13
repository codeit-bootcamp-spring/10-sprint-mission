package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthDto.UserLoginRequestDto;
import com.sprint.mission.discodeit.dto.AuthDto.UserLoginResponseDto;
import com.sprint.mission.discodeit.dto.user.UserResponseDto;

public interface AuthService {
    UserLoginResponseDto login(UserLoginRequestDto dto);
}
