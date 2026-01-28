package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserDTO;

public interface AuthService {
    UserDTO.Response login(UserDTO.Login request);
}
