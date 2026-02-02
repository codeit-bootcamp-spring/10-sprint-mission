package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserServiceDTO.*;

public interface UserService extends DomainService<UserResponse, UserCreation, UserInfoUpdate> {
    UserResponse find(String username, String password);
}
