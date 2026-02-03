package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.LoginDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.UUID;

public interface AuthService {
    User login(LoginDto.LoginRequest request);

}
