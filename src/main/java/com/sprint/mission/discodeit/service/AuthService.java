package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.LoginRequestDto;

import java.util.UUID;

public interface AuthService {
    UUID login(LoginRequestDto loginRequestDto);
}
