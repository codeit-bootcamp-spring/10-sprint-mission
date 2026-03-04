package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authdto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;

public interface AuthService {

    public UserResponseDTO login(LoginRequestDTO authDTO);

}
