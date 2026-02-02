package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.UserLogin;

public interface AuthService {
    void login(UserLogin model);
}
