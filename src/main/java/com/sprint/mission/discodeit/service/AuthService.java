package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.UserLogin;

import java.io.IOException;

public interface AuthService {
    boolean login(UserLogin model) throws IOException;
}
