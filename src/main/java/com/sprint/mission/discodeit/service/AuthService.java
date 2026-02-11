package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserResponse;

public interface AuthService {
    // 일치하는 유저가 있는 경우 UserResponse를 반환, 없으면 예외 발생
    UserResponse login(LoginRequest request);
}
