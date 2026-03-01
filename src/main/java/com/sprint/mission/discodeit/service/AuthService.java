package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequest;
import com.sprint.mission.discodeit.dto.user.User;

public interface AuthService {

  User login(LoginRequest request);
}
