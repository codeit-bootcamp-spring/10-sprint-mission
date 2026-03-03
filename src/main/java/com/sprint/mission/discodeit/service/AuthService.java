package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserResponse;

public interface AuthService {
    UserResponse login(LoginRequest request);
}
