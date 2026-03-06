package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authdto.LoginRequestDTO;
import com.sprint.mission.discodeit.dto.userdto.UserDto;

public interface AuthService {

    public UserDto login(LoginRequestDTO authDTO);

}
