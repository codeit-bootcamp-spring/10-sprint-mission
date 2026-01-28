package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.DTO.UserDTO.UserLoginDTO;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    public User login(UserLoginDTO userLoginDTO);

}
