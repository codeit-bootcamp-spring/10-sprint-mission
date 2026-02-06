package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.UserServiceDTO.*;

import java.util.List;

public interface UserService extends DomainService<UserResponse, UserCreation, UserInfoUpdate> {
    UserResponse find(UsernamePassword model);
    List<UserResponse> findAll();
}
