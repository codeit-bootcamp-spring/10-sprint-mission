package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.AuthServiceDTO.LoginRequest;

import java.io.IOException;

public interface AuthService {
    boolean login(LoginRequest request) throws IOException;
}
