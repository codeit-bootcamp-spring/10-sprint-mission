package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.UserWithOnlineResponse;

public interface AuthService {

    UserWithOnlineResponse login(LoginRequest loginRequest);
}
