package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.request.AuthLoginRequestDTO;
import com.sprint.mission.discodeit.dto.response.AuthResponseDTO;

public interface AuthService {
    AuthResponseDTO login(AuthLoginRequestDTO userLoginRequestDTO);
}
