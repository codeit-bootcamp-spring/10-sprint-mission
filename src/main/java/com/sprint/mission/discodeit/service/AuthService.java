package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authdto.AuthDTO;
import com.sprint.mission.discodeit.dto.userdto.UserResponseDTO;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    public UserResponseDTO login(AuthDTO authDTO);

}
