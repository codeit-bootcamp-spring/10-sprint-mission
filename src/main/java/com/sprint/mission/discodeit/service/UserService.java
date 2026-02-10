package com.sprint.mission.discodeit.service;

import com.sprint.mission.discodeit.dto.user.UserCreateRequest;
import com.sprint.mission.discodeit.dto.user.UserDto;
import com.sprint.mission.discodeit.dto.user.UserUpdateRequest;

import java.util.UUID;
import java.util.List;


public interface UserService {
    UserDto create(UserCreateRequest request);

    UserDto findById(UUID id);

    List<UserDto> findAll();

    UserDto update(UUID id, UserUpdateRequest request);

    void deleteById(UUID id);
}
