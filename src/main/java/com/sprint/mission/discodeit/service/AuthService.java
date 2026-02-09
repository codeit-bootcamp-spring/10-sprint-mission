package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.auth.AuthLoginRequestDTO;
import com.sprint.mission.discodeit.dto.response.UserResponseDTO;

public interface AuthService {
    // 로그인
    UserResponseDTO login(AuthLoginRequestDTO authLoginRequestDTO);
}
