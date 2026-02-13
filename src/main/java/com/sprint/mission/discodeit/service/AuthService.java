package com.sprint.mission.discodeit.service;


import com.sprint.mission.discodeit.dto.auth.AuthLoginRequestDto;
import com.sprint.mission.discodeit.dto.auth.AuthResponseDto;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    AuthResponseDto login(AuthLoginRequestDto authLoginRequestDto);
}
