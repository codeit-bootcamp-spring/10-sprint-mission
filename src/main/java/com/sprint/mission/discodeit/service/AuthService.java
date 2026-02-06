package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.UserLogin;

public interface AuthService {
    boolean login(UserLogin model);
}
