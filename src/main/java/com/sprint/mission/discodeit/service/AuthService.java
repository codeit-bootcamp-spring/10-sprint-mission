package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserInfoDto;
import com.sprint.mission.discodeit.dto.UserLoginDto;
import com.sprint.mission.discodeit.entity.User;

public interface AuthService {
    UserInfoDto login(UserLoginDto request);
}
