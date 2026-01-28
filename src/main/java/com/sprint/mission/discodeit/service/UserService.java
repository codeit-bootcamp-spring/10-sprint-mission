package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.CreateUserRequest;
import com.sprint.mission.discodeit.dto.UpdateUserRequest;
import com.sprint.mission.discodeit.dto.UserDto;
import com.sprint.mission.discodeit.entity.User;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserDto create(CreateUserRequest request);
    UserDto find(UUID userId);
    List<UserDto> findAll();
    UserDto update(UUID userId, UpdateUserRequest request);
    void delete(UUID userId);
}
