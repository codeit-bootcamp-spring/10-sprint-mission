package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.authdto.AuthDTO;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    public User login(AuthDTO authDTO);

}
