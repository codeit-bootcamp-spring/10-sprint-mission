package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.auth.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.auth.LoginResponseDTO;
import com.sprint.mission.discodeit.dto.user.UserDto;

public interface AuthService {
    UserDto login(LoginRequestDTO dto);
}
