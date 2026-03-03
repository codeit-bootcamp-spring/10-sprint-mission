package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserServiceDTO.UserResponse;
import com.sprint.mission.discodeit.dto.user.command.UserCreateCommand;
import com.sprint.mission.discodeit.dto.user.command.UserUpdateCommand;
import com.sprint.mission.discodeit.dto.user.request.UserFindRequest;

import java.util.List;

public interface UserService extends DomainService<UserResponse, UserCreateCommand, UserUpdateCommand> {
    UserResponse find(UserFindRequest request);

    List<UserResponse> findAll();

}
